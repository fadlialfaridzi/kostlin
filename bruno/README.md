# Kostlin API - Bruno Collection

Koleksi API untuk menguji backend Kostlin menggunakan Bruno.

## Setup

1. Install [Bruno](https://www.usebruno.com/) jika belum terinstall
2. Buka Bruno dan import folder `bruno` ini sebagai collection
3. Pastikan backend server berjalan di `http://localhost:5000`

## Environment

Collection ini menggunakan environment `local` dengan variabel:
- `baseUrl`: http://localhost:5000
- `token`: Token JWT (akan diisi setelah login)

## Cara Menggunakan

### 1. Authentication
- **Register**: Daftar user baru
- **Login**: Login dan copy token dari response ke environment variable `token`
- **Forgot Password**: Request OTP untuk reset password
- **Verify OTP**: Verifikasi OTP yang diterima
- **Reset Password**: Reset password setelah OTP terverifikasi
- **Get Profile**: Lihat profil user (perlu token)

### 2. Kos Endpoints
- **Get All Kos**: Dapatkan semua kos dengan optional filters
- **Get Popular Kos**: Dapatkan kos populer
- **Get Recommended Kos**: Dapatkan kos recommended
- **Search Kos**: Cari kos berdasarkan keyword
- **Get Kos By ID**: Dapatkan detail kos berdasarkan ID

### 3. Favorites Endpoints (Perlu Authentication)
- **Add to Favorite**: Tambahkan kos ke favorit
- **Remove from Favorite**: Hapus kos dari favorit
- **Get User Favorites**: Dapatkan semua favorit user
- **Check Favorite Status**: Cek apakah kos sudah di favorit

## Tips

1. Setelah login berhasil, copy token dari response dan paste ke environment variable `token` di Bruno
2. Untuk endpoint yang memerlukan authentication, pastikan token sudah di-set
3. Gunakan parameter query untuk filter pada Get All Kos
4. Untuk development, OTP akan muncul di console backend (tidak dikirim via email)

## Base URL

Default: `http://localhost:5000`

Jika menggunakan emulator Android, ganti dengan:
- Android Emulator: `http://10.0.2.2:5000`
- Physical Device: `http://<IP_ADDRESS_PC>:5000`

