# 🚀 Cara Running Backend Kostlin API

Panduan lengkap untuk menjalankan backend server dan testing dengan Bruno.

## 📋 Prerequisites

1. ✅ Node.js terinstall (versi 14 atau lebih baru)
2. ✅ MySQL/phpMyAdmin sudah terinstall dan berjalan
3. ✅ Database `kostlin` sudah dibuat di phpMyAdmin

## 🔧 Langkah 1: Setup Konfigurasi Database

1. Buka folder `backend/`
2. Buat file `.env` (copy dari `.env.example` jika ada, atau buat manual)
3. Edit file `.env` dengan konfigurasi database Anda:

```env
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=password_anda
DB_NAME=kostlin
PORT=5000
JWT_SECRET=kostlin_secret_key_2024
JWT_EXPIRES_IN=7d
NODE_ENV=development
```

**Catatan:** 
- Ganti `DB_PASSWORD` dengan password MySQL Anda
- Jika tidak ada password, biarkan kosong: `DB_PASSWORD=`

## 📦 Langkah 2: Install Dependencies

Buka terminal/command prompt di folder `backend/` dan jalankan:

```bash
npm install
```

Tunggu sampai semua package terinstall (sekitar 1-2 menit).

## 🗄️ Langkah 3: Setup Database (Migration)

Jalankan migration untuk membuat tabel-tabel di database:

```bash
npm run migrate
```

**Output yang diharapkan:**
```
🔄 Connecting to database...
✅ Connected to database: kostlin
📝 Found X SQL statements to execute...

✅ Created table: users
✅ Created table: otp_codes
✅ Created table: kos_properties
✅ Created table: kos_facilities
✅ Created table: reviews
✅ Created table: favorites
✅ Created table: bookings
✅ Inserted data into: kos_properties
✅ Inserted data into: kos_facilities

✨ Migration completed successfully!
```

## 🚀 Langkah 4: Jalankan Backend Server

### Development Mode (Recommended)
Jalankan dengan nodemon (auto-restart saat ada perubahan):

```bash
npm run dev
```

### Production Mode
Jalankan tanpa auto-restart:

```bash
npm start
```

**Output yang diharapkan:**
```
✅ Database connected successfully
🚀 Kostlin API server listening on port 5000
📡 API Base URL: http://localhost:5000
📚 API Documentation: http://localhost:5000/api
```

**✅ Backend sudah berjalan!** Biarkan terminal ini tetap terbuka.

## 🧪 Langkah 5: Test dengan Bruno

### 5.1 Install Bruno

1. Download Bruno dari: https://www.usebruno.com/
2. Install Bruno sesuai OS Anda
3. Buka aplikasi Bruno

### 5.2 Import Collection

1. Di Bruno, klik **"Open Collection"** atau **"Import Collection"**
2. Pilih folder `bruno/` dari project ini
3. Collection akan muncul di sidebar kiri

### 5.3 Setup Environment

1. Di Bruno, klik tab **"Environments"** (biasanya di bagian bawah)
2. Pilih environment **"local"**
3. Pastikan variabel:
   - `baseUrl`: `http://localhost:5000`
   - `token`: (kosongkan dulu, akan diisi setelah login)

### 5.4 Test API Endpoints

#### A. Test Health Check
1. Klik folder **"Kos"** → **"Get All Kos"**
2. Klik tombol **"Send"** atau tekan `Ctrl+Enter`
3. Seharusnya mendapat response dengan data kos

#### B. Test Register & Login
1. Buka **"Auth"** → **"Register"**
2. Edit body JSON jika perlu:
   ```json
   {
     "fullName": "Test User",
     "email": "test@example.com",
     "password": "password123"
   }
   ```
3. Klik **"Send"**
4. Copy `token` dari response
5. Buka **"Auth"** → **"Login"**
6. Test login dengan email dan password yang sama
7. Copy `token` dari response login

#### C. Set Token untuk Protected Routes
1. Klik tab **"Environments"**
2. Di environment **"local"**, paste token ke variabel `token`
3. Sekarang semua endpoint yang memerlukan auth akan otomatis menggunakan token ini

#### D. Test Protected Endpoints
1. Buka **"Auth"** → **"Get Profile"**
2. Klik **"Send"** → Seharusnya mendapat data profil user
3. Buka **"Favorites"** → Test semua endpoint favorites

## 📝 Contoh Testing Flow

### Flow Lengkap:

1. **Register User Baru**
   ```
   POST /api/auth/register
   Body: {
     "fullName": "John Doe",
     "email": "john@example.com",
     "password": "password123"
   }
   ```

2. **Login**
   ```
   POST /api/auth/login
   Body: {
     "email": "john@example.com",
     "password": "password123"
   }
   Response: { "data": { "token": "..." } }
   ```

3. **Copy Token** → Paste ke environment variable `token` di Bruno

4. **Get All Kos**
   ```
   GET /api/kos
   ```

5. **Add to Favorites**
   ```
   POST /api/favorites/1
   Header: Authorization: Bearer <token>
   ```

6. **Get User Favorites**
   ```
   GET /api/favorites
   Header: Authorization: Bearer <token>
   ```

## 🔍 Troubleshooting

### ❌ Error: "Cannot find module 'mysql2'"
**Solusi:** Jalankan `npm install` di folder backend

### ❌ Error: "Database connection error"
**Solusi:** 
- Pastikan MySQL service berjalan
- Cek konfigurasi di file `.env`
- Pastikan database `kostlin` sudah dibuat

### ❌ Error: "Port 5000 already in use"
**Solusi:** 
- Ganti PORT di file `.env` (misal: `PORT=5001`)
- Atau tutup aplikasi yang menggunakan port 5000

### ❌ Error: "Table already exists"
**Solusi:** 
- Ini normal jika tabel sudah ada
- Migration akan skip tabel yang sudah ada

### ❌ CORS Error di Browser
**Solusi:** 
- Backend sudah dikonfigurasi untuk allow all origins
- Pastikan backend server berjalan
- Cek base URL di Bruno environment

### ❌ 401 Unauthorized
**Solusi:**
- Pastikan token sudah di-set di environment variable `token`
- Pastikan format header: `Bearer <token>`
- Token mungkin expired, login lagi untuk dapat token baru

## 📊 Checklist

- [ ] Node.js terinstall
- [ ] MySQL berjalan
- [ ] Database `kostlin` dibuat
- [ ] File `.env` dibuat dan dikonfigurasi
- [ ] Dependencies terinstall (`npm install`)
- [ ] Migration berhasil (`npm run migrate`)
- [ ] Backend server berjalan (`npm run dev`)
- [ ] Bruno terinstall
- [ ] Collection di-import ke Bruno
- [ ] Environment variable di-set
- [ ] Test endpoint berhasil

## 🎯 Quick Start Commands

```bash
# 1. Masuk ke folder backend
cd backend

# 2. Install dependencies
npm install

# 3. Setup database (migration)
npm run migrate

# 4. Jalankan server
npm run dev
```

## 📞 Base URL

- **Local:** `http://localhost:5000`
- **Android Emulator:** `http://10.0.2.2:5000`
- **Physical Device:** `http://<IP_PC_ANDA>:5000`

## 🎉 Selesai!

Backend sudah berjalan dan siap digunakan. Anda bisa:
- ✅ Test semua endpoint dengan Bruno
- ✅ Integrasikan dengan aplikasi Android
- ✅ Develop dan test fitur baru

Selamat coding! 🚀

