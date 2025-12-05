# ✅ Konfirmasi: Flow Register dari UI ke Database

## 🎯 Pernyataan
**Ketika menekan button "Create An Account" di UI, data pengguna akan masuk ke database pada table `users`**

## ✅ Status: BENAR dan SUDAH TERIMPLEMENTASI

---

## 📊 Visual Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    REGISTER SCREEN (UI)                     │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Full Name: [________________]                        │  │
│  │  Email:    [________________]                        │  │
│  │  Password: [________________] 👁                      │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │     [  Create An Account  ]  ← USER KLIK INI        │  │
│  └──────────────────────────────────────────────────────┘  │
└───────────────────────────┬───────────────────────────────┘
                              │
                              │ onClick()
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    AUTH VIEWMODEL                           │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  viewModel.register(                                 │  │
│  │    fullName, email, password                         │  │
│  │  )                                                    │  │
│  └──────────────────────────────────────────────────────┘  │
└───────────────────────────┬───────────────────────────────┘
                              │
                              │ HTTP POST Request
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    RETROFIT CLIENT                          │
│  POST http://localhost:5000/api/auth/register              │
│  Body: {                                                    │
│    "fullName": "John Doe",                                  │
│    "email": "john@example.com",                             │
│    "password": "password123"                                 │
│  }                                                           │
└───────────────────────────┬───────────────────────────────┘
                              │
                              │ Network Request
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              EXPRESS BACKEND SERVER                         │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  1. Validasi input                                    │  │
│  │  2. Cek email sudah terdaftar?                        │  │
│  │  3. Hash password dengan bcrypt                      │  │
│  │  4. INSERT INTO users (...)                           │  │
│  └──────────────────────────────────────────────────────┘  │
└───────────────────────────┬───────────────────────────────┘
                              │
                              │ SQL INSERT Query
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    MYSQL DATABASE                           │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Table: users                                         │  │
│  │  ┌────┬─────────────┬──────────────────┬──────────┐  │  │
│  │  │ id │ full_name   │ email            │ password │  │  │
│  │  ├────┼─────────────┼──────────────────┼──────────┤  │  │
│  │  │ 1  │ John Doe    │ john@example.com │ $2a$10...│  │  │
│  │  └────┴─────────────┴──────────────────┴──────────┘  │  │
│  │                                                        │  │
│  │  ✅ DATA TERSIMPAN DI SINI                            │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔍 Detail Implementasi

### 1. UI Layer (RegisterScreen.kt)

**File:** `app/src/main/java/com/example/kostlin/ui/screen/autentikasi/Register.kt`

```kotlin
Button(
    onClick = {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill all fields"
        } else {
            showTermsDialog = true
        }
    }
) {
    Text("Create An Account")
}
```

**Ketika user klik "Agree" di Terms dialog:**
```kotlin
viewModel.register(trimmedName, trimmedEmail, password) {
    onRegisterSuccess(trimmedName, trimmedEmail)
}
```

### 2. ViewModel Layer (AuthViewModel.kt)

**File:** `app/src/main/java/com/example/kostlin/ui/viewmodel/AuthViewModel.kt`

```kotlin
fun register(fullName: String, email: String, password: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        authRepository.register(fullName, email, password)
            .onSuccess {
                // Success - data sudah tersimpan di database
                onSuccess()
            }
            .onFailure { exception ->
                // Error handling
            }
    }
}
```

### 3. Repository Layer (AuthRepository.kt)

**File:** `app/src/main/java/com/example/kostlin/data/repository/AuthRepository.kt`

```kotlin
suspend fun register(fullName: String, email: String, password: String): Result<AuthResponse> {
    val request = RegisterRequest(fullName, email, password)
    val response = apiService.register(request)  // HTTP POST
    
    if (response.isSuccessful && response.body()?.success == true) {
        val authData = response.body()!!.data!!
        tokenManager.saveToken(authData.token)
        tokenManager.saveUserInfo(authData.userId, authData.fullName, authData.email)
        return Result.success(authData)
    }
    // ...
}
```

### 4. Network Layer (ApiService.kt)

**File:** `app/src/main/java/com/example/kostlin/data/api/ApiService.kt`

```kotlin
@POST("api/auth/register")
suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>
```

### 5. Backend Controller (authController.js)

**File:** `backend/controllers/authController.js`

```javascript
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
    
    // 3. ✅ INSERT KE DATABASE - TABLE USERS
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

### 6. Database Schema (schema.sql)

**File:** `backend/database/schema.sql`

```sql
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## ✅ Verifikasi Data Tersimpan

### Query Database untuk Verify

```sql
USE kostlin;

-- Lihat semua users
SELECT id, full_name, email, created_at FROM users;

-- Lihat user tertentu
SELECT * FROM users WHERE email = 'john@example.com';

-- Hitung total users
SELECT COUNT(*) as total_users FROM users;
```

### Expected Result Setelah Register

```
+----+-----------+-------------------+---------------------+
| id | full_name | email             | created_at          |
+----+-----------+-------------------+---------------------+
|  1 | John Doe  | john@example.com  | 2024-01-01 12:00:00 |
+----+-----------+-------------------+---------------------+
```

---

## 🔐 Security Features

1. **Password Hashing**
   - Password di-hash dengan bcrypt sebelum INSERT
   - Tidak pernah disimpan dalam bentuk plain text

2. **Email Uniqueness**
   - Email harus unique (UNIQUE constraint di database)
   - Cek di backend sebelum INSERT

3. **SQL Injection Protection**
   - Menggunakan prepared statements
   - Parameter di-bind secara aman

4. **Input Validation**
   - Client-side validation di Android
   - Server-side validation di backend

---

## 📋 Checklist Flow

- [x] UI: Button "Create An Account" ada
- [x] UI: Terms dialog muncul setelah klik button
- [x] UI: User klik "Agree" → trigger register
- [x] ViewModel: AuthViewModel.register() dipanggil
- [x] Repository: AuthRepository.register() dipanggil
- [x] Network: HTTP POST ke `/api/auth/register`
- [x] Backend: authController.register() memproses request
- [x] Backend: Validasi email tidak duplikat
- [x] Backend: Hash password dengan bcrypt
- [x] **Database: INSERT INTO users (...)**
- [x] Database: Data tersimpan di table `users`
- [x] Backend: Generate JWT token
- [x] Backend: Return response dengan token
- [x] Android: Token disimpan di SharedPreferences
- [x] Android: User info disimpan di SharedPreferences

---

## 🎯 Kesimpulan

**✅ BENAR: Ketika menekan button "Create An Account" di UI, data pengguna akan masuk ke database pada table `users`**

**Flow sudah lengkap dan terintegrasi dengan benar:**
1. ✅ UI → ViewModel → Repository → Network
2. ✅ HTTP POST Request ke Backend
3. ✅ Backend memproses dan INSERT ke database
4. ✅ Data tersimpan di table `users`
5. ✅ Response dikembalikan dengan token
6. ✅ Token disimpan di Android untuk authentication

**Semua layer sudah terhubung dengan benar!** 🎉

