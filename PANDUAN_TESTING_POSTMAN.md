# Panduan Testing API dengan Postman

## 📋 Persiapan

### 1. Pastikan Backend Berjalan
```bash
cd backend
npm start
# atau
npm run dev
```

Backend akan berjalan di: `http://localhost:5000`

### 2. Install Postman
- Download Postman dari: https://www.postman.com/downloads/
- Atau gunakan Postman Web: https://web.postman.com/

## 🚀 Setup Postman

### 1. Buat Environment Baru

1. Klik **Environments** di sidebar kiri
2. Klik **+** untuk membuat environment baru
3. Beri nama: `Kostlin Local`
4. Tambahkan variables berikut:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `baseUrl` | `http://localhost:5000` | `http://localhost:5000` |
| `token` | (kosong) | (kosong) |

5. Klik **Save**

### 2. Aktifkan Environment

- Pilih environment `Kostlin Local` di dropdown di kanan atas Postman

## 📁 Buat Collection

1. Klik **Collections** di sidebar
2. Klik **+** untuk membuat collection baru
3. Beri nama: `Kostlin API`
4. Klik **Save**

## 🔐 Authentication Endpoints

### 1. Register User

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/register`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "password": "password123"
  }
  ```

**Expected Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "userId": 1,
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Tips:** Copy token dari response dan paste ke environment variable `token`

### 2. Login

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/login`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "email": "john.doe@example.com",
    "password": "password123"
  }
  ```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": 1,
    "fullName": "John Doe",
    "email": "john.doe@example.com",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Tips:** Copy token dan update environment variable `token`

### 3. Get Profile (Protected)

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/auth/profile`
- **Headers:**
  ```
  Authorization: Bearer {{token}}
  Content-Type: application/json
  ```

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "full_name": "John Doe",
    "email": "john.doe@example.com",
    "phone": null,
    "avatar_url": null,
    "is_verified": false,
    "created_at": "2024-01-01T00:00:00.000Z"
  }
}
```

### 4. Forgot Password

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/forgot-password`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "email": "john.doe@example.com"
  }
  ```

**Expected Response:**
```json
{
  "success": true,
  "message": "OTP sent to email",
  "otp": "123456"
}
```

**Note:** OTP hanya muncul di development mode

### 5. Verify OTP

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/verify-otp`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "email": "john.doe@example.com",
    "code": "123456"
  }
  ```

### 6. Reset Password

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/auth/reset-password`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
  ```json
  {
    "email": "john.doe@example.com",
    "password": "newpassword123"
  }
  ```

## 🏠 Kos Endpoints

### 1. Get All Kos

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/kos`
- **Headers:** (tidak perlu)

**Query Parameters (Optional):**
- `type`: `PUTRA`, `PUTRI`, atau `CAMPUR`
- `minPrice`: `500000`
- `maxPrice`: `1000000`
- `search`: `"kos putri"`
- `popular`: `true`
- `recommended`: `true`

**Example URL dengan query:**
```
{{baseUrl}}/api/kos?type=PUTRI&minPrice=500000&maxPrice=1000000
```

### 2. Get Kos By ID

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/kos/1`
- **Headers:** (tidak perlu)

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Kos Putri Mama Faiz",
    "location": "Limau Manis, Padang",
    "price": "Rp 800.000/Bulan",
    "pricePerMonth": 800000,
    "rating": "4.5",
    "ratingValue": 4.5,
    "type": "PUTRI",
    "facilities": ["WiFi", "AC", "Kamar Mandi Dalam"],
    "description": "Kos putri yang nyaman...",
    "imageUrl": "https://...",
    "isPopular": true,
    "isRecommended": true,
    "reviewCount": 10,
    "reviews": [...]
  }
}
```

### 3. Get Popular Kos

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/kos/popular`
- **Headers:** (tidak perlu)

### 4. Get Recommended Kos

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/kos/recommended`
- **Headers:** (tidak perlu)

### 5. Search Kos

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/kos/search?q=kos putri`
- **Headers:** (tidak perlu)

**Query Parameters:**
- `q`: search query (required)

## ❤️ Favorite Endpoints (Protected)

**Note:** Semua endpoint favorites memerlukan authentication token!

### 1. Add to Favorite

**Request:**
- **Method:** `POST`
- **URL:** `{{baseUrl}}/api/favorites/1`
- **Headers:**
  ```
  Authorization: Bearer {{token}}
  Content-Type: application/json
  ```

**Note:** `1` adalah ID kos yang ingin ditambahkan ke favorites

### 2. Remove from Favorite

**Request:**
- **Method:** `DELETE`
- **URL:** `{{baseUrl}}/api/favorites/1`
- **Headers:**
  ```
  Authorization: Bearer {{token}}
  Content-Type: application/json
  ```

### 3. Get User Favorites

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/favorites`
- **Headers:**
  ```
  Authorization: Bearer {{token}}
  Content-Type: application/json
  ```

**Expected Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "kosProperty": {
        "id": 1,
        "name": "Kos Putri Mama Faiz",
        ...
      },
      "dateAdded": "Hari ini",
      "status": "ACTIVE"
    }
  ],
  "count": 1
}
```

### 4. Check Favorite Status

**Request:**
- **Method:** `GET`
- **URL:** `{{baseUrl}}/api/favorites/check/1`
- **Headers:**
  ```
  Authorization: Bearer {{token}}
  Content-Type: application/json
  ```

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "isFavorite": true
  }
}
```

## 🔧 Tips & Tricks

### 1. Menggunakan Environment Variables

Setelah login, copy token dari response dan:
1. Klik environment `Kostlin Local`
2. Update value untuk variable `token`
3. Semua request berikutnya akan otomatis menggunakan token ini

### 2. Pre-request Script untuk Auto Token

Anda bisa menambahkan pre-request script di collection level:

```javascript
// Auto-set token dari environment
pm.request.headers.add({
    key: 'Authorization',
    value: 'Bearer ' + pm.environment.get('token')
});
```

### 3. Test Scripts

Tambahkan test scripts untuk validasi response:

```javascript
// Test untuk login
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has token", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.data).to.have.property('token');
    
    // Auto-save token
    if (jsonData.data.token) {
        pm.environment.set("token", jsonData.data.token);
    }
});
```

### 4. Collection Runner

Gunakan Collection Runner untuk test semua endpoints sekaligus:
1. Klik collection `Kostlin API`
2. Klik **Run**
3. Pilih requests yang ingin di-test
4. Klik **Run Kostlin API**

## 📝 Contoh Workflow Testing

### Step 1: Register User Baru
```
POST {{baseUrl}}/api/auth/register
Body: {
  "fullName": "Test User",
  "email": "test@example.com",
  "password": "test123"
}
```
→ Copy token dari response

### Step 2: Update Environment Variable
- Buka environment `Kostlin Local`
- Paste token ke variable `token`
- Save

### Step 3: Test Get Profile
```
GET {{baseUrl}}/api/auth/profile
Headers: Authorization: Bearer {{token}}
```
→ Harus return user profile

### Step 4: Test Get All Kos
```
GET {{baseUrl}}/api/kos
```
→ Harus return list kos

### Step 5: Test Add Favorite
```
POST {{baseUrl}}/api/favorites/1
Headers: Authorization: Bearer {{token}}
```
→ Harus return success

### Step 6: Test Get Favorites
```
GET {{baseUrl}}/api/favorites
Headers: Authorization: Bearer {{token}}
```
→ Harus return list favorites dengan kos ID 1

## 🐛 Troubleshooting

### Error: "Connection refused"
- Pastikan backend server sedang berjalan
- Periksa URL: harus `http://localhost:5000`

### Error: "Unauthorized" atau 401
- Pastikan token sudah di-set di environment
- Pastikan format header: `Authorization: Bearer <token>`
- Token mungkin sudah expired, login lagi untuk dapat token baru

### Error: "Route not found" atau 404
- Periksa URL endpoint
- Pastikan menggunakan `/api/` prefix
- Periksa method (GET, POST, DELETE)

### Error: "Validation failed"
- Periksa body request
- Pastikan semua field required sudah diisi
- Periksa format JSON

## 📚 Export/Import Collection

### Export Collection ke File
1. Klik collection `Kostlin API`
2. Klik **...** (three dots)
3. Pilih **Export**
4. Pilih format `Collection v2.1`
5. Save file

### Import Collection
1. Klik **Import** di Postman
2. Pilih file collection yang sudah di-export
3. Collection akan muncul di sidebar

## ✅ Checklist Testing

- [ ] Backend server berjalan di port 5000
- [ ] Environment `Kostlin Local` sudah dibuat
- [ ] Collection `Kostlin API` sudah dibuat
- [ ] Test Register - berhasil
- [ ] Test Login - berhasil dan token tersimpan
- [ ] Test Get Profile - berhasil dengan token
- [ ] Test Get All Kos - berhasil
- [ ] Test Get Kos By ID - berhasil
- [ ] Test Get Popular Kos - berhasil
- [ ] Test Get Recommended Kos - berhasil
- [ ] Test Search Kos - berhasil
- [ ] Test Add Favorite - berhasil dengan token
- [ ] Test Get Favorites - berhasil dengan token
- [ ] Test Remove Favorite - berhasil dengan token
- [ ] Test Check Favorite Status - berhasil dengan token

---

**Selamat Testing!** 🎉

Jika ada masalah, periksa:
1. Backend logs di terminal
2. Postman Console (View → Show Postman Console)
3. Network tab di browser (jika menggunakan Postman Web)

