# Kostlin Backend API

Backend API untuk aplikasi Kostlin menggunakan Node.js, Express, dan MySQL.

## Fitur

- ✅ Authentication (Register, Login, Forgot Password dengan OTP)
- ✅ Kos Management (CRUD operations)
- ✅ Favorites Management
- ✅ Reviews & Ratings
- ✅ Search & Filter
- ✅ JWT Authentication

## Setup

### 1. Install Dependencies

```bash
npm install
```

### 2. Konfigurasi Database

Copy file `.env.example` menjadi `.env` dan sesuaikan konfigurasi:

```bash
cp .env.example .env
```

Edit file `.env`:
```env
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=kostlin
PORT=5000
JWT_SECRET=your_secret_key
```

### 3. Setup Database

Pastikan database `kostlin` sudah dibuat di MySQL/phpMyAdmin, kemudian jalankan migration:

```bash
npm run migrate
```

### 4. Jalankan Server

Development mode (dengan nodemon):
```bash
npm run dev
```

Production mode:
```bash
npm start
```

Server akan berjalan di `http://localhost:5000`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register user baru
- `POST /api/auth/login` - Login user
- `POST /api/auth/forgot-password` - Request OTP untuk reset password
- `POST /api/auth/verify-otp` - Verifikasi OTP
- `POST /api/auth/reset-password` - Reset password
- `GET /api/auth/profile` - Get user profile (protected)

### Kos
- `GET /api/kos` - Get all kos dengan optional filters
- `GET /api/kos/popular` - Get popular kos
- `GET /api/kos/recommended` - Get recommended kos
- `GET /api/kos/search?q=keyword` - Search kos
- `GET /api/kos/:id` - Get kos by ID

### Favorites (Protected)
- `POST /api/favorites/:kosId` - Add to favorites
- `DELETE /api/favorites/:kosId` - Remove from favorites
- `GET /api/favorites` - Get user favorites
- `GET /api/favorites/check/:kosId` - Check favorite status

## Testing dengan Bruno

Gunakan koleksi Bruno yang sudah disediakan di folder `bruno/` untuk menguji semua endpoint.

1. Install [Bruno](https://www.usebruno.com/)
2. Buka Bruno dan import folder `bruno/`
3. Pastikan backend server berjalan
4. Mulai testing!

## Database Schema

Database menggunakan MySQL dengan tabel:
- `users` - Data user
- `otp_codes` - OTP untuk reset password
- `kos_properties` - Data kos
- `kos_facilities` - Fasilitas kos
- `reviews` - Review dan rating
- `favorites` - Favorit user
- `bookings` - Booking kos

Lihat `database/schema.sql` untuk detail schema.

## Development

### Struktur Folder

```
backend/
├── config/          # Konfigurasi (database, dll)
├── controllers/     # Controller untuk setiap route
├── database/        # Schema dan migration
├── middleware/      # Middleware (auth, validation)
├── routes/          # Route definitions
└── server.js        # Entry point
```

## Notes

- Di development mode, OTP akan muncul di console (tidak dikirim via email)
- Pastikan MySQL service berjalan sebelum menjalankan server
- Gunakan environment variables untuk konfigurasi sensitif

