# Booking Flow - Complete Checkout System

## Deskripsi
Sistem booking lengkap dengan alur multi-step untuk memesan kos dari halaman detail hingga konfirmasi sukses.

## Alur Booking

```
DetailKosScreen
    ↓
[Klik "Pesan Sekarang" button]
    ↓
RequestToBookScreen (Permintaan Booking)
    ├─ Pilih tanggal masuk/keluar
    ├─ Atur kapasitas
    └─ Lihat total pembayaran
    ↓
[Klik tanggal untuk membuka calendar]
    ↓
SelectDateScreen (Pilih Tanggal)
    ├─ Calendar dengan navigasi bulan
    ├─ Pilih tanggal
    └─ Klik "Lanjut"
    ↓
RequestToBookScreen (dengan tanggal terpilih)
    ↓
[Klik "Booking" button]
    ↓
CheckoutScreen (Pesanan)
    ├─ Detail kos
    ├─ Pesanan kamu (tanggal, kapasitas, tipe kamar, telepon)
    ├─ Kontak pemilik
    └─ Ringkasan harga
    ↓
[Klik "Booking Sekarang" button]
    ↓
BookingLoadingScreen (Loading 3 detik)
    ├─ Animasi hourglass
    └─ Cancel button
    ↓
BookingSuccessScreen (Booking Selesai)
    ├─ Animasi checkmark
    ├─ Pesan sukses
    └─ Button "Lanjut"
    ↓
Kembali ke DetailKosScreen
```

## File Structure

```
booking/
├── BookingData.kt              # Data classes (BookingRequest, BookingDetail)
├── RequestToBookScreen.kt      # Step 1: Permintaan Booking
├── SelectDateScreen.kt         # Step 2: Pilih Tanggal (Calendar Modal)
├── CheckoutScreen.kt           # Step 3: Pesanan (Checkout Detail)
├── BookingLoadingScreen.kt     # Step 4: Loading Animation
├── BookingSuccessScreen.kt     # Step 5: Booking Selesai
└── BOOKING_README.md           # Dokumentasi ini
```

## Component Details

### 1. BookingData.kt

**BookingRequest**
```kotlin
data class BookingRequest(
    val kosId: Int,
    val checkInDate: LocalDate? = null,
    val checkOutDate: LocalDate? = null,
    val capacity: Int = 1,
    val roomType: String = "3 x 4 Meter"
)
```

**BookingDetail**
```kotlin
data class BookingDetail(
    val kosId: Int,
    val kosName: String,
    val location: String,
    val pricePerMonth: Int,
    val rating: Float,
    val imageUrl: String? = null,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val capacity: Int,
    val roomType: String,
    val ownerEmail: String,
    val ownerPhone: String,
    val userPhone: String
)
```

### 2. RequestToBookScreen

**Fitur:**
- Header dengan back button dan menu
- Section Tanggal dengan 2 card (Masuk/Keluar)
- Section Kapasitas dengan +/- button
- Total pembayaran calculation
- Booking button (disabled jika tanggal belum dipilih)

**Props:**
- `kosProperty: KosProperty` - Data kos
- `onBackClick: () -> Unit` - Callback back
- `onSelectDateClick: () -> Unit` - Callback buka calendar
- `onBookingClick: (BookingRequest) -> Unit` - Callback booking

### 3. SelectDateScreen

**Fitur:**
- Modal calendar dengan navigasi bulan
- Day headers (Min, Sen, Sel, dst)
- Calendar grid dengan date selection
- Highlight untuk tanggal hari ini dan selected date
- Batal dan Lanjut button

**Props:**
- `onDateSelected: (LocalDate) -> Unit` - Callback tanggal dipilih
- `onBackClick: () -> Unit` - Callback back/batal

### 4. CheckoutScreen

**Fitur:**
- Property info card dengan image, nama, lokasi, harga, rating
- Pesanan Kamu section:
  - Tanggal (check-in - check-out)
  - Kapasitas
  - Tipe kamar
  - Telepon user
- Kontak Pemilik section:
  - Email pemilik
  - Telepon pemilik
- Price summary:
  - Harga per hari
  - Jumlah hari
  - Total pembayaran
- Booking Sekarang button

**Props:**
- `bookingDetail: BookingDetail` - Detail booking
- `onBackClick: () -> Unit` - Callback back
- `onConfirmBooking: () -> Unit` - Callback confirm booking

### 5. BookingLoadingScreen

**Fitur:**
- Semi-transparent overlay
- Loading card dengan hourglass animation
- "Loading..." text
- Cancel button
- Auto-complete setelah 3 detik

**Props:**
- `onLoadingComplete: () -> Unit` - Callback loading selesai
- `onCancel: () -> Unit` - Callback cancel

### 6. BookingSuccessScreen

**Fitur:**
- Full screen dengan white background
- Success icon (checkmark) dengan scale animation
- "Booking Selesai" title
- "Hubungi Pemilik" subtitle
- Lanjut button

**Props:**
- `onContinue: () -> Unit` - Callback continue

## State Management di DetailKosScreen

```kotlin
// Booking flow states
var bookingStep by remember { mutableStateOf<BookingStep?>(null) }
var bookingRequest by remember { mutableStateOf<BookingRequest?>(null) }
var selectedCheckInDate by remember { mutableStateOf<LocalDate?>(null) }
var selectedCheckOutDate by remember { mutableStateOf<LocalDate?>(null) }

enum class BookingStep {
    REQUEST_TO_BOOK, SELECT_DATE, CHECKOUT, LOADING, SUCCESS
}
```

## User Interactions

### Step 1: Request to Book
- ✅ Klik "Pesan Sekarang" → Buka RequestToBookScreen
- ✅ Klik card tanggal → Buka SelectDateScreen
- ✅ Atur kapasitas dengan +/- button
- ✅ Lihat total pembayaran real-time
- ✅ Klik "Booking" → Lanjut ke Checkout

### Step 2: Select Date
- ✅ Navigate bulan dengan arrow button
- ✅ Klik tanggal untuk select
- ✅ Klik "Lanjut" → Kembali ke RequestToBook dengan tanggal terpilih
- ✅ Klik "Batal" → Kembali ke RequestToBook

### Step 3: Checkout
- ✅ Lihat detail kos dan booking
- ✅ Lihat ringkasan harga
- ✅ Klik "Booking Sekarang" → Mulai loading
- ✅ Klik back → Kembali ke RequestToBook

### Step 4: Loading
- ✅ Animasi loading 3 detik
- ✅ Klik "Cancel" → Kembali ke Checkout

### Step 5: Success
- ✅ Lihat animasi checkmark
- ✅ Klik "Lanjut" → Kembali ke DetailKosScreen

## Styling

### Colors
- Primary: `Color(0xFF5876FF)` (Blue)
- Background: `Color.White`
- Text Primary: `Color(0xFF1B2633)` (Dark Blue)
- Text Secondary: `Color(0xFF6B7280)` (Gray)
- Success: `Color(0xFF10B981)` (Green)
- Error: `Color(0xFFEF4444)` (Red)
- Light Background: `Color(0xFFF5F5F5)`

### Typography
- Header: `titleLarge` dengan `FontWeight.Bold`
- Section Title: `titleMedium` dengan `FontWeight.Bold`
- Body: `bodyMedium` dengan `FontWeight.SemiBold`
- Small: `bodySmall`

## Animations

### SelectDateScreen
- Calendar grid fade-in
- Date selection highlight

### BookingLoadingScreen
- Hourglass rotation (infinite)
- Duration: 1500ms

### BookingSuccessScreen
- Checkmark scale animation
- Duration: 500ms

## Date Calculation

```kotlin
// Total days
fun getTotalDays(): Int {
    return (checkOutDate.toEpochDay() - checkInDate.toEpochDay()).toInt()
}

// Total price
fun getTotalPrice(): Int {
    return pricePerMonth * getTotalDays() / 30
}
```

## Dependencies
- Jetpack Compose
- Material3
- Java Time (LocalDate, YearMonth)
- Compose Icons

## Testing Checklist

- [ ] Klik "Pesan Sekarang" → RequestToBookScreen terbuka
- [ ] Klik card tanggal → SelectDateScreen (calendar) terbuka
- [ ] Pilih tanggal → Tanggal ter-update di RequestToBookScreen
- [ ] Atur kapasitas → Total pembayaran ter-update
- [ ] Klik "Booking" → CheckoutScreen terbuka dengan detail benar
- [ ] Lihat ringkasan harga → Calculation benar
- [ ] Klik "Booking Sekarang" → BookingLoadingScreen terbuka
- [ ] Tunggu 3 detik → BookingSuccessScreen terbuka otomatis
- [ ] Klik "Lanjut" → Kembali ke DetailKosScreen
- [ ] Klik back di setiap step → Kembali ke step sebelumnya

## Future Enhancements

- Add payment method selection
- Add promo code input
- Add user profile auto-fill
- Add booking history
- Add cancellation policy
- Add terms & conditions modal
- Add email confirmation
- Add SMS notification
- Add real-time availability check
- Add guest count validation
