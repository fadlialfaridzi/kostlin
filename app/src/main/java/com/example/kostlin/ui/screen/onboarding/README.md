# Onboarding Screen

## Deskripsi
Onboarding screen menampilkan 3 halaman yang memandu pengguna sebelum masuk ke halaman login. Setiap halaman menampilkan:
- Background image dengan overlay gelap
- Judul dan deskripsi
- Indikator halaman (dots)
- Tombol untuk lanjut ke halaman berikutnya

## Fitur

### 1. **Animasi Halus dan Ringan**
- Fade in/out untuk transisi background image
- Slide horizontal untuk konten text
- Scale animation untuk indikator dots
- Durasi animasi: 500ms untuk performa optimal

### 2. **Material Design 3**
- Menggunakan Material3 components (Button, Text)
- Color scheme sesuai dengan theme aplikasi
- Rounded corners dan spacing yang konsisten

### 3. **Responsive Layout**
- Menggunakan Jetpack Compose untuk layout yang fleksibel
- Mendukung berbagai ukuran layar
- Padding dan spacing yang sesuai

### 4. **Image Loading**
- Menggunakan Coil untuk loading image dari URL
- Support untuk berbagai format image
- Lazy loading untuk performa

## Struktur File

```
onboarding/
├── OnboardingScreen.kt    # Main composable untuk onboarding
└── README.md             # Dokumentasi ini
```

## Integrasi dengan MainActivity

Onboarding screen ditampilkan setelah splash screen selesai:

```kotlin
if (showSplash) {
    SplashScreen(onSplashFinished = { showSplash = false })
} else if (showOnboarding) {
    OnboardingScreen(onOnboardingFinished = { showOnboarding = false })
} else {
    // Login/Home screen
}
```

## Customization

### Mengubah Konten Onboarding
Edit `pages` list di dalam `OnboardingScreen()`:

```kotlin
val pages = listOf(
    OnboardingPage(
        title = "Judul Halaman 1",
        description = "Deskripsi halaman 1",
        imageUrl = "https://..."
    ),
    // ... halaman lainnya
)
```

### Mengubah Warna
- Button color: `ButtonBlue` (dari theme)
- Text color: `Color.White` untuk judul, `Color.White.copy(alpha = 0.9f)` untuk deskripsi
- Indicator: `ButtonBlue` untuk active, `Color.White.copy(alpha = 0.5f)` untuk inactive

### Mengubah Durasi Animasi
Edit `tween(durationMillis = 500)` di setiap AnimatedVisibility dan animateFloatAsState

## Dependencies
- Jetpack Compose
- Material3
- Coil (untuk image loading)

## Preview
Gunakan `OnboardingScreenPreview()` untuk melihat preview di Android Studio
