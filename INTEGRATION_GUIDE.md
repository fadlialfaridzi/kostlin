# Panduan Integrasi Frontend Android dengan Backend API

Panduan ini menjelaskan cara menghubungkan aplikasi Android Kostlin dengan backend API.

## Prerequisites

1. ✅ Backend server sudah berjalan di `http://localhost:5000`
2. ✅ Database MySQL sudah di-setup dan di-migrate
3. ✅ Bruno collection sudah tersedia untuk testing API

## Konfigurasi Base URL

### Untuk Android Emulator
Gunakan: `http://10.0.2.2:5000`

### Untuk Physical Device
Gunakan IP address PC Anda di jaringan lokal, contoh: `http://192.168.1.100:5000`

## Menambahkan Dependencies

Tambahkan dependencies berikut ke `app/build.gradle.kts`:

```kotlin
dependencies {
    // ... existing dependencies ...
    
    // Retrofit untuk HTTP client
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // OkHttp untuk interceptor dan logging
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Coroutines untuk async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
}
```

## Struktur API Service

### 1. Buat file `ApiService.kt`

```kotlin
package com.example.kostlin.data.api

import com.example.kostlin.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth endpoints
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Unit>>
    
    @POST("auth/verify-otp")
    suspend fun verifyOTP(@Body request: VerifyOTPRequest): Response<ApiResponse<Unit>>
    
    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Unit>>
    
    @GET("auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ApiResponse<UserProfile>>
    
    // Kos endpoints
    @GET("kos")
    suspend fun getAllKos(
        @Query("type") type: String? = null,
        @Query("minPrice") minPrice: Int? = null,
        @Query("maxPrice") maxPrice: Int? = null,
        @Query("search") search: String? = null,
        @Query("popular") popular: Boolean? = null,
        @Query("recommended") recommended: Boolean? = null
    ): Response<ApiResponse<List<KosProperty>>>
    
    @GET("kos/popular")
    suspend fun getPopularKos(): Response<ApiResponse<List<KosProperty>>>
    
    @GET("kos/recommended")
    suspend fun getRecommendedKos(): Response<ApiResponse<List<KosProperty>>>
    
    @GET("kos/search")
    suspend fun searchKos(@Query("q") query: String): Response<ApiResponse<List<KosProperty>>>
    
    @GET("kos/{id}")
    suspend fun getKosById(@Path("id") id: Int): Response<ApiResponse<KosDetail>>
    
    // Favorites endpoints
    @POST("favorites/{kosId}")
    suspend fun addToFavorite(
        @Header("Authorization") token: String,
        @Path("kosId") kosId: Int
    ): Response<ApiResponse<Unit>>
    
    @DELETE("favorites/{kosId}")
    suspend fun removeFromFavorite(
        @Header("Authorization") token: String,
        @Path("kosId") kosId: Int
    ): Response<ApiResponse<Unit>>
    
    @GET("favorites")
    suspend fun getUserFavorites(@Header("Authorization") token: String): Response<ApiResponse<List<FavoriteKos>>>
    
    @GET("favorites/check/{kosId}")
    suspend fun checkFavoriteStatus(
        @Header("Authorization") token: String,
        @Path("kosId") kosId: Int
    ): Response<ApiResponse<FavoriteStatusResponse>>
}
```

### 2. Buat file `ApiClient.kt`

```kotlin
package com.example.kostlin.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Ganti dengan base URL yang sesuai
    // Untuk emulator: http://10.0.2.2:5000/api/
    // Untuk device fisik: http://<IP_ADDRESS>:5000/api/
    private const val BASE_URL = "http://10.0.2.2:5000/api/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
```

### 3. Buat Data Classes untuk Request/Response

```kotlin
// Request models
data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class VerifyOTPRequest(
    val email: String,
    val code: String
)

data class ResetPasswordRequest(
    val email: String,
    val password: String
)

// Response models
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: AuthData
)

data class AuthData(
    val userId: Int,
    val fullName: String,
    val email: String,
    val token: String
)

data class UserProfile(
    val id: Int,
    val full_name: String,
    val email: String,
    val phone: String?,
    val avatar_url: String?,
    val is_verified: Boolean,
    val created_at: String
)

data class FavoriteStatusResponse(
    val isFavorite: Boolean
)
```

### 4. Buat Repository untuk mengelola API calls

```kotlin
package com.example.kostlin.data.repository

import com.example.kostlin.data.api.ApiClient
import com.example.kostlin.data.api.*
import com.example.kostlin.data.model.*

class KosRepository {
    private val apiService = ApiClient.apiService
    
    suspend fun getAllKos(
        type: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        search: String? = null,
        popular: Boolean? = null,
        recommended: Boolean? = null
    ): Result<List<KosProperty>> {
        return try {
            val response = apiService.getAllKos(type, minPrice, maxPrice, search, popular, recommended)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPopularKos(): Result<List<KosProperty>> {
        return try {
            val response = apiService.getPopularKos()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(email: String, password: String): Result<AuthData> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Tambahkan method lainnya...
}
```

### 5. Buat SharedPreferences untuk menyimpan token

```kotlin
package com.example.kostlin.data.local

import android.content.Context
import android.content.SharedPreferences

class AuthPreferences(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("kostlin_prefs", Context.MODE_PRIVATE)
    
    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }
    
    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }
    
    fun clearToken() {
        prefs.edit().remove("auth_token").apply()
    }
    
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
```

## Testing dengan Bruno

Sebelum mengintegrasikan ke Android, test semua endpoint menggunakan Bruno:

1. Buka Bruno
2. Import collection dari folder `bruno/`
3. Test endpoint satu per satu
4. Pastikan semua endpoint berfungsi dengan baik
5. Copy response format untuk mapping ke data class Android

## Langkah Integrasi

1. **Setup Dependencies**: Tambahkan Retrofit dan OkHttp ke `build.gradle.kts`
2. **Buat API Service**: Buat interface `ApiService` dengan semua endpoint
3. **Setup ApiClient**: Konfigurasi Retrofit dengan base URL yang sesuai
4. **Buat Data Classes**: Mapping request/response models
5. **Buat Repository**: Layer untuk mengelola API calls
6. **Update ViewModel**: Gunakan repository untuk fetch data
7. **Update UI**: Replace dummy data dengan data dari API

## Troubleshooting

### Connection Error
- Pastikan backend server berjalan
- Cek base URL (gunakan `10.0.2.2` untuk emulator)
- Cek firewall dan network settings

### CORS Error
- Backend sudah dikonfigurasi untuk allow all origins
- Jika masih error, pastikan CORS middleware aktif

### Authentication Error
- Pastikan token disimpan dengan benar
- Cek format Authorization header: `Bearer <token>`
- Pastikan token belum expired

## Next Steps

1. Implement API service layer
2. Replace `KosDummyData` dengan API calls
3. Implement authentication flow
4. Add error handling dan loading states
5. Implement caching jika diperlukan

