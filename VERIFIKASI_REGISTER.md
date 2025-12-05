# Verifikasi Register - Data Tersimpan di Database

## ✅ Flow Register yang Sudah Diimplementasikan

### 1. Proses Register di Backend

Ketika user melakukan register melalui API, berikut yang terjadi:

```javascript
// File: backend/controllers/authController.js

1. Validasi email sudah terdaftar atau belum
   → SELECT id FROM users WHERE email = ?

2. Hash password menggunakan bcrypt
   → Password tidak disimpan dalam bentuk plain text

3. Insert data ke database
   → INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)

4. Generate JWT token
   → Token digunakan untuk authentication

5. Return response dengan data user dan token
```

### 2. Data yang Tersimpan di Database

Setelah register berhasil, data berikut tersimpan di tabel `users`:

| Field | Value | Keterangan |
|-------|-------|------------|
| `id` | Auto increment | ID unik user |
| `full_name` | Dari request body | Nama lengkap user |
| `email` | Dari request body | Email user (unique) |
| `password` | Hashed dengan bcrypt | Password yang sudah di-hash |
| `phone` | NULL (default) | Bisa diisi kemudian |
| `avatar_url` | NULL (default) | Bisa diisi kemudian |
| `is_verified` | FALSE (default) | Status verifikasi |
| `created_at` | Timestamp otomatis | Waktu register |
| `updated_at` | Timestamp otomatis | Waktu update terakhir |

## 🔍 Cara Verifikasi Data Tersimpan

### Method 1: Menggunakan MySQL Client

```sql
-- Connect ke database
mysql -u root -p

-- Pilih database
USE kostlin;

-- Lihat semua users
SELECT id, full_name, email, phone, is_verified, created_at 
FROM users;

-- Lihat user tertentu berdasarkan email
SELECT id, full_name, email, created_at 
FROM users 
WHERE email = 'john.doe@example.com';

-- Lihat detail lengkap user
SELECT * FROM users WHERE id = 1;
```

### Method 2: Menggunakan API Get Profile

Setelah register/login, test endpoint Get Profile:

```http
GET {{baseUrl}}/api/auth/profile
Authorization: Bearer {{token}}
```

Response akan menampilkan data user yang sudah tersimpan.

### Method 3: Menggunakan Postman

1. **Register user baru:**
   ```
   POST {{baseUrl}}/api/auth/register
   Body: {
     "fullName": "Test User",
     "email": "test@example.com",
     "password": "test123"
   }
   ```

2. **Login dengan user yang baru dibuat:**
   ```
   POST {{baseUrl}}/api/auth/login
   Body: {
     "email": "test@example.com",
     "password": "test123"
   }
   ```

3. **Get Profile untuk verify:**
   ```
   GET {{baseUrl}}/api/auth/profile
   Authorization: Bearer {{token}}
   ```

Jika semua berhasil, berarti data sudah tersimpan dengan benar.

## 🔐 Security Features

### 1. Password Hashing
- Password **TIDAK** disimpan dalam bentuk plain text
- Menggunakan **bcrypt** dengan salt rounds 10
- Contoh: `password123` → `$2a$10$abcdefghijklmnopqrstuvwxyz...`

### 2. Email Validation
- Email harus **unique** (tidak boleh duplikat)
- Jika email sudah terdaftar, akan return error 400

### 3. SQL Injection Protection
- Menggunakan **prepared statements** dengan `pool.execute()`
- Parameter di-bind secara aman

## 📊 Contoh Query untuk Monitoring

### Lihat semua users yang terdaftar
```sql
SELECT 
    id,
    full_name,
    email,
    is_verified,
    DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%s') as registered_at
FROM users
ORDER BY created_at DESC;
```

### Hitung total users
```sql
SELECT COUNT(*) as total_users FROM users;
```

### Lihat users yang terdaftar hari ini
```sql
SELECT * FROM users 
WHERE DATE(created_at) = CURDATE();
```

### Lihat users yang belum verified
```sql
SELECT id, full_name, email, created_at 
FROM users 
WHERE is_verified = FALSE;
```

## 🧪 Testing Register

### Test Case 1: Register User Baru (Success)
```json
Request:
POST /api/auth/register
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}

Expected Response:
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

Database Check:
SELECT * FROM users WHERE email = 'john@example.com';
→ Harus ada 1 record dengan full_name = 'John Doe'
```

### Test Case 2: Register dengan Email Duplikat (Error)
```json
Request:
POST /api/auth/register
{
  "fullName": "Jane Doe",
  "email": "john@example.com",  // Email sudah terdaftar
  "password": "password456"
}

Expected Response:
{
  "success": false,
  "message": "Email already registered"
}

Database Check:
SELECT COUNT(*) FROM users WHERE email = 'john@example.com';
→ Harus tetap 1 (tidak ada duplikat)
```

### Test Case 3: Register dengan Data Tidak Lengkap (Error)
```json
Request:
POST /api/auth/register
{
  "fullName": "Test User",
  "email": "",  // Email kosong
  "password": "test123"
}

Expected Response:
Validation error dari express-validator
```

## 🔄 Flow Lengkap Register

```
1. User mengisi form register di Android app
   ↓
2. Android app mengirim POST request ke /api/auth/register
   ↓
3. Backend menerima request
   ↓
4. Validasi input (express-validator)
   ↓
5. Cek email sudah terdaftar atau belum
   ↓
6. Hash password dengan bcrypt
   ↓
7. INSERT ke database (tabel users)
   ↓
8. Generate JWT token
   ↓
9. Return response dengan user data dan token
   ↓
10. Android app menyimpan token
   ↓
11. User otomatis login
```

## 📝 Checklist Verifikasi

Setelah register, pastikan:

- [ ] Response status code = 201 (Created)
- [ ] Response memiliki `success: true`
- [ ] Response memiliki `data.token` (JWT token)
- [ ] Data tersimpan di database tabel `users`
- [ ] Password tersimpan dalam bentuk hashed (bukan plain text)
- [ ] Email unique (tidak bisa register email yang sama 2x)
- [ ] `created_at` terisi otomatis dengan timestamp
- [ ] Token bisa digunakan untuk Get Profile endpoint

## 🐛 Troubleshooting

### Data tidak tersimpan di database
1. Periksa koneksi database di `.env`
2. Periksa apakah tabel `users` sudah dibuat
3. Periksa backend logs untuk error messages
4. Pastikan database migration sudah dijalankan

### Error "Email already registered"
- Normal jika email sudah terdaftar
- Gunakan email berbeda untuk test
- Atau hapus user dari database untuk test ulang

### Password tidak ter-hash
- Pastikan bcryptjs sudah terinstall
- Periksa kode di authController.js line 30

## 💡 Tips

1. **Untuk Development:**
   - Bisa langsung query database untuk verify
   - Gunakan MySQL Workbench atau phpMyAdmin untuk visualisasi

2. **Untuk Production:**
   - Jangan expose database langsung
   - Gunakan API Get Profile untuk verify
   - Monitor melalui admin panel (jika ada)

3. **Security:**
   - Password selalu di-hash sebelum disimpan
   - Jangan pernah log password dalam bentuk plain text
   - Gunakan HTTPS untuk production

---

**Kesimpulan:** Register sudah benar diimplementasikan dan data akan tersimpan ke database dengan aman. Password di-hash, email unique, dan semua data tersimpan dengan benar.

