# Flow Register dari Android ke Database

## 📱 Flow Lengkap: Button "Create An Account" → Database

### Step-by-Step Flow

```
1. User mengisi form di RegisterScreen
   ├── Full Name: "John Doe"
   ├── Email: "john@example.com"
   └── Password: "password123"
   ↓
2. User klik button "Create An Account"
   ↓
3. Validasi input (client-side)
   ├── Cek apakah semua field terisi
   └── Jika kosong → tampilkan error "Please fill all fields"
   ↓
4. Tampilkan Terms & Conditions Dialog
   ├── User harus setuju Terms & Conditions
   └── Button "Agree" atau "Disagree"
   ↓
5. User klik "Agree" di dialog
   ↓
6. AuthViewModel.register() dipanggil
   ├── Trim fullName dan email
   └── Panggil authRepository.register()
   ↓
7. AuthRepository.register()
   ├── Buat RegisterRequest object
   ├── Panggil apiService.register() via Retrofit
   └── Kirim HTTP POST request ke backend
   ↓
8. HTTP Request ke Backend
   POST http://localhost:5000/api/auth/register
   Body: {
     "fullName": "John Doe",
     "email": "john@example.com",
     "password": "password123"
   }
   ↓
9. Backend menerima request
   ├── Validasi input (express-validator)
   ├── Cek email sudah terdaftar atau belum
   ├── Hash password dengan bcrypt
   └── INSERT ke database
   ↓
10. Database INSERT
    INSERT INTO users (full_name, email, password) 
    VALUES ('John Doe', 'john@example.com', '$2a$10$...')
    ↓
11. Data tersimpan di database ✅
    └── Tabel: users
        ├── id: 1 (auto increment)
        ├── full_name: "John Doe"
        ├── email: "john@example.com"
        ├── password: "$2a$10$..." (hashed)
        ├── created_at: "2024-01-01 12:00:00"
        └── is_verified: false
   ↓
12. Backend generate JWT token
    └── Token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   ↓
13. Backend return response
    {
      "success": true,
      "message": "User registered successfully",
      "data": {
        "userId": 1,
        "fullName": "John Doe",
        "email": "john@example.com",
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
      }
    }
   ↓
14. Android menerima response
    ├── AuthRepository menyimpan token ke TokenManager
    ├── TokenManager.saveToken() → SharedPreferences
    ├── TokenManager.saveUserInfo() → SharedPreferences
    └── Return Result.success()
   ↓
15. AuthViewModel update UI state
    ├── isLoading = false
    ├── isSuccess = true
    └── userFullName = "John Doe"
   ↓
16. onRegisterSuccess() callback dipanggil
    └── Navigate ke Login screen atau Home screen
   ↓
17. User berhasil register ✅
    └── Token tersimpan untuk authentication selanjutnya
```

## 🔍 Detail Implementasi

### 1. RegisterScreen.kt - Button Click Handler

```kotlin
// File: app/src/main/java/com/example/kostlin/ui/screen/autentikasi/Register.kt

Button(
    onClick = {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill all fields"
        } else {
            showTermsDialog = true  // Tampilkan dialog Terms
        }
    }
)
```

### 2. Terms Dialog - Agree Button

```kotlin
// Ketika user klik "Agree" di Terms dialog

TextButton(
    onClick = {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim()
        showTermsDialog = false
        
        // Panggil ViewModel untuk register
        viewModel.register(trimmedName, trimmedEmail, password) {
            onRegisterSuccess(trimmedName, trimmedEmail)
        }
    }
)
```

### 3. AuthViewModel.register()

```kotlin
// File: app/src/main/java/com/example/kostlin/ui/viewmodel/AuthViewModel.kt

fun register(fullName: String, email: String, password: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        
        authRepository.register(fullName, email, password)
            .onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    userFullName = it.fullName
                )
                onSuccess()  // Callback untuk navigate
            }
            .onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = exception.message ?: "Registration failed"
                )
            }
    }
}
```

### 4. AuthRepository.register()

```kotlin
// File: app/src/main/java/com/example/kostlin/data/repository/AuthRepository.kt

suspend fun register(fullName: String, email: String, password: String): Result<AuthResponse> {
    return try {
        val request = RegisterRequest(fullName, email, password)
        val response = apiService.register(request)  // HTTP POST request
        
        if (response.isSuccessful && response.body()?.success == true) {
            val authData = response.body()!!.data!!
            
            // Simpan token dan user info
            tokenManager.saveToken(authData.token)
            tokenManager.saveUserInfo(authData.userId, authData.fullName, authData.email)
            
            Result.success(authData)
        } else {
            val errorMessage = response.body()?.message ?: "Registration failed"
            Result.failure(Exception(errorMessage))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### 5. ApiService.register()

```kotlin
// File: app/src/main/java/com/example/kostlin/data/api/ApiService.kt

@POST("api/auth/register")
suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>
```

### 6. Backend - authController.register()

```javascript
// File: backend/controllers/authController.js

const register = async (req, res) => {
    const { fullName, email, password } = req.body;
    
    // 1. Cek email sudah terdaftar
    const [existingUsers] = await pool.execute(
        'SELECT id FROM users WHERE email = ?',
        [email]
    );
    
    if (existingUsers.length > 0) {
        return res.status(400).json({
            success: false,
            message: 'Email already registered'
        });
    }
    
    // 2. Hash password
    const hashedPassword = await bcrypt.hash(password, 10);
    
    // 3. INSERT ke database ✅
    const [result] = await pool.execute(
        'INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)',
        [fullName, email, hashedPassword]
    );
    
    // 4. Generate token
    const token = jwt.sign(
        { userId: result.insertId, email },
        process.env.JWT_SECRET,
        { expiresIn: '7d' }
    );
    
    // 5. Return response
    res.status(201).json({
        success: true,
        message: 'User registered successfully',
        data: {
            userId: result.insertId,
            fullName,
            email,
            token
        }
    });
};
```

## ✅ Verifikasi Data Tersimpan

### Method 1: Query Database

```sql
USE kostlin;

-- Lihat semua users
SELECT id, full_name, email, created_at FROM users;

-- Lihat user yang baru register
SELECT * FROM users WHERE email = 'john@example.com';
```

### Method 2: Test API Get Profile

Setelah register, token sudah tersimpan. Test dengan:

```http
GET http://localhost:5000/api/auth/profile
Authorization: Bearer <token_dari_response_register>
```

### Method 3: Check SharedPreferences (Android)

Token tersimpan di:
- Key: `auth_token`
- File: `kostlin_prefs.xml`
- Location: `/data/data/com.example.kostlin/shared_prefs/kostlin_prefs.xml`

## 🔐 Security Features

1. **Password Hashing**
   - Password di-hash dengan bcrypt sebelum disimpan
   - Tidak pernah disimpan dalam bentuk plain text

2. **Email Validation**
   - Email harus unique
   - Tidak bisa register email yang sama 2x

3. **Input Validation**
   - Client-side validation di Android
   - Server-side validation di backend

4. **Token Storage**
   - Token disimpan di SharedPreferences
   - Token digunakan untuk authenticated requests

## 📊 Data Flow Diagram

```
┌─────────────────┐
│  RegisterScreen │
│  (UI Layer)     │
└────────┬────────┘
         │
         │ onClick "Create An Account"
         ▼
┌─────────────────┐
│  AuthViewModel  │
│  (ViewModel)    │
└────────┬────────┘
         │
         │ register()
         ▼
┌─────────────────┐
│ AuthRepository  │
│  (Repository)   │
└────────┬────────┘
         │
         │ HTTP POST
         ▼
┌─────────────────┐
│   Retrofit      │
│   (Network)     │
└────────┬────────┘
         │
         │ POST /api/auth/register
         ▼
┌─────────────────┐
│  Express Server │
│   (Backend)     │
└────────┬────────┘
         │
         │ INSERT INTO users
         ▼
┌─────────────────┐
│   MySQL DB      │
│  (Database)     │
└─────────────────┘
         │
         │ Data tersimpan ✅
         ▼
    ┌─────────┐
    │ Success │
    └─────────┘
```

## 🧪 Testing Flow

### Test Case 1: Register Berhasil

1. **Input:**
   - Full Name: "Test User"
   - Email: "test@example.com"
   - Password: "test123"

2. **Expected:**
   - Button "Create An Account" → Terms dialog muncul
   - Klik "Agree" → Loading indicator muncul
   - Response success → Navigate ke Login/Home
   - Data tersimpan di database
   - Token tersimpan di SharedPreferences

3. **Verify:**
   ```sql
   SELECT * FROM users WHERE email = 'test@example.com';
   ```
   → Harus ada 1 record

### Test Case 2: Email Duplikat

1. **Input:**
   - Email yang sudah terdaftar

2. **Expected:**
   - Error message: "Email already registered"
   - Data tidak tersimpan (tidak ada duplikat)

### Test Case 3: Field Kosong

1. **Input:**
   - Salah satu field kosong

2. **Expected:**
   - Error: "Please fill all fields"
   - Terms dialog tidak muncul

## 📝 Checklist Implementasi

- [x] RegisterScreen UI sudah dibuat
- [x] Button "Create An Account" sudah ada
- [x] Terms & Conditions dialog sudah ada
- [x] AuthViewModel sudah terintegrasi
- [x] AuthRepository sudah terintegrasi
- [x] Retrofit API service sudah dibuat
- [x] Backend endpoint sudah dibuat
- [x] Database INSERT sudah bekerja
- [x] Token storage sudah bekerja
- [x] Error handling sudah ada
- [x] Loading state sudah ada

## 🎯 Kesimpulan

**Ya, ketika Anda menekan button "Create An Account":**

1. ✅ Data akan dikirim ke backend via HTTP POST
2. ✅ Backend akan menyimpan data ke database MySQL
3. ✅ Password akan di-hash dengan bcrypt
4. ✅ Token JWT akan di-generate dan dikembalikan
5. ✅ Token akan tersimpan di Android SharedPreferences
6. ✅ User info akan tersimpan di SharedPreferences
7. ✅ User akan otomatis "logged in" setelah register

**Flow sudah lengkap dan terintegrasi dengan benar!** 🎉

