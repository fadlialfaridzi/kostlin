# Panduan Integrasi Android dengan Backend Express

File ini menjelaskan bagaimana aplikasi Android Jetpack Compose telah diintegrasikan dengan backend Node.js Express.

## Struktur Integrasi

### 1. Dependencies yang Ditambahkan

Dependencies berikut telah ditambahkan ke `app/build.gradle.kts`:
- **Retrofit 2.9.0** - Untuk HTTP client
- **OkHttp 4.12.0** - Untuk HTTP logging dan interceptors
- **Gson** - Untuk JSON serialization/deserialization
- **Coroutines** - Untuk asynchronous operations
- **ViewModel Compose** - Untuk state management

### 2. Struktur File yang Dibuat

```
app/src/main/java/com/example/kostlin/
├── data/
│   ├── api/
│   │   ├── ApiService.kt          # Interface untuk semua API endpoints
│   │   ├── RetrofitClient.kt      # Konfigurasi Retrofit instance
│   │   └── model/
│   │       └── ApiResponse.kt      # Data models untuk API responses
│   ├── local/
│   │   └── TokenManager.kt        # SharedPreferences untuk token storage
│   └── repository/
│       ├── AuthRepository.kt      # Repository untuk authentication
│       ├── KosRepository.kt       # Repository untuk kos data
│       └── FavoriteRepository.kt  # Repository untuk favorites
├── ui/
│   └── viewmodel/
│       ├── AuthViewModel.kt       # ViewModel untuk authentication
│       ├── KosViewModel.kt        # ViewModel untuk kos data
│       └── FavoriteViewModel.kt   # ViewModel untuk favorites
└── di/
    └── AppModule.kt               # Dependency injection module
```

### 3. Konfigurasi Network

#### Base URL Configuration

File `RetrofitClient.kt` berisi konfigurasi BASE_URL. **PENTING**: Sesuaikan dengan setup Anda:

```kotlin
// Untuk Android Emulator (default)
private const val BASE_URL = "http://10.0.2.2:5000/"

// Untuk Device Fisik - ganti dengan IP komputer Anda di jaringan yang sama
// private const val BASE_URL = "http://192.168.1.XXX:5000/"
```

**Cara menemukan IP komputer Anda:**
- Windows: Buka Command Prompt, ketik `ipconfig`, cari "IPv4 Address"
- Mac/Linux: Buka Terminal, ketik `ifconfig` atau `ip addr`

#### Network Security Config

File `network_security_config.xml` telah dibuat untuk mengizinkan HTTP traffic (untuk development). Untuk production, gunakan HTTPS.

### 4. Cara Menggunakan

#### Authentication

Login screen sudah terintegrasi dengan backend:

```kotlin
// Login otomatis menggunakan AuthViewModel
// Token akan disimpan otomatis setelah login berhasil
```

#### Mengambil Data Kos

HomeScreen sudah menggunakan KosViewModel untuk mengambil data dari backend:

```kotlin
// Data popular kos dan recommended kos di-load otomatis saat screen dibuka
// Filter berdasarkan kategori juga sudah terintegrasi
```

#### Favorites

FavoriteViewModel tersedia untuk menambah/menghapus favorites. Pastikan user sudah login terlebih dahulu.

### 5. Testing

#### Menjalankan Backend

1. Buka terminal di folder `backend`
2. Pastikan database sudah dikonfigurasi di `.env`
3. Jalankan: `npm start` atau `npm run dev`

#### Menjalankan Android App

1. Buka project di Android Studio
2. Pastikan BASE_URL di `RetrofitClient.kt` sesuai dengan setup Anda
3. Untuk emulator: gunakan `http://10.0.2.2:5000/`
4. Untuk device fisik: gunakan IP komputer Anda, contoh `http://192.168.1.100:5000/`
5. Build dan run aplikasi

### 6. Troubleshooting

#### Error: "Unable to resolve host" atau "Connection refused"

- Pastikan backend server sedang berjalan
- Periksa BASE_URL di `RetrofitClient.kt`
- Untuk device fisik, pastikan Android device dan komputer dalam jaringan WiFi yang sama
- Periksa firewall yang mungkin memblokir koneksi

#### Error: "Network security config"

- Pastikan `network_security_config.xml` sudah dibuat
- Pastikan `AndroidManifest.xml` sudah mengacu ke file tersebut

#### Token tidak tersimpan

- Periksa apakah `TokenManager` sudah diinisialisasi dengan benar
- Pastikan SharedPreferences berfungsi dengan baik

### 7. API Endpoints yang Tersedia

#### Authentication
- `POST /api/auth/register` - Register user baru
- `POST /api/auth/login` - Login user
- `POST /api/auth/forgot-password` - Request OTP
- `POST /api/auth/verify-otp` - Verify OTP
- `POST /api/auth/reset-password` - Reset password
- `GET /api/auth/profile` - Get user profile (requires auth)

#### Kos
- `GET /api/kos` - Get all kos (dengan optional filters)
- `GET /api/kos/:id` - Get kos by ID
- `GET /api/kos/popular` - Get popular kos
- `GET /api/kos/recommended` - Get recommended kos
- `GET /api/kos/search?q=query` - Search kos

#### Favorites (requires auth)
- `POST /api/favorites/:kosId` - Add to favorites
- `DELETE /api/favorites/:kosId` - Remove from favorites
- `GET /api/favorites` - Get user favorites
- `GET /api/favorites/check/:kosId` - Check favorite status

### 8. Next Steps

1. Update screens lain (Register, ForgotPassword, dll) untuk menggunakan AuthViewModel
2. Integrasikan FavoriteViewModel ke DetailKosScreen
3. Tambahkan error handling yang lebih baik di UI
4. Tambahkan loading indicators
5. Implementasi refresh/pull-to-refresh
6. Tambahkan caching untuk offline support

### 9. Catatan Penting

- **Development**: Aplikasi saat ini menggunakan HTTP. Untuk production, gunakan HTTPS
- **Security**: Token disimpan di SharedPreferences. Pertimbangkan menggunakan EncryptedSharedPreferences untuk production
- **Error Handling**: Tambahkan user-friendly error messages di UI
- **Testing**: Test dengan berbagai skenario (no internet, server down, dll)

## Support

Jika ada masalah, periksa:
1. Logcat untuk melihat error messages
2. Backend logs untuk melihat request yang diterima
3. Network security config
4. BASE_URL configuration

