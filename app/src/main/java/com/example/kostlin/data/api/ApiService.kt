package com.example.kostlin.data.api

import com.example.kostlin.data.api.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Auth Endpoints
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>
    
    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Any>>
    
    @POST("api/auth/verify-otp")
    suspend fun verifyOTP(@Body request: VerifyOTPRequest): Response<ApiResponse<Any>>
    
    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Any>>
    
    @GET("api/auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ApiResponse<UserProfile>>
    
    // Kos Endpoints
    @GET("api/kos")
    suspend fun getAllKos(
        @Query("type") type: String? = null,
        @Query("minPrice") minPrice: Int? = null,
        @Query("maxPrice") maxPrice: Int? = null,
        @Query("search") search: String? = null,
        @Query("popular") popular: Boolean? = null,
        @Query("recommended") recommended: Boolean? = null
    ): Response<ApiResponse<List<KosPropertyDto>>>
    
    @POST("api/kos")
    suspend fun createKos(@Body request: CreateKosRequest): Response<ApiResponse<KosPropertyDto>>
    
    @GET("api/kos/{id}")
    suspend fun getKosById(@Path("id") id: Int): Response<ApiResponse<KosPropertyDto>>
    
    @GET("api/kos/popular")
    suspend fun getPopularKos(): Response<ApiResponse<List<KosPropertyDto>>>
    
    @GET("api/kos/recommended")
    suspend fun getRecommendedKos(): Response<ApiResponse<List<KosPropertyDto>>>
    
    @GET("api/kos/search")
    suspend fun searchKos(@Query("q") query: String): Response<ApiResponse<List<KosPropertyDto>>>
    
    // Favorite Endpoints (all require authentication)
    @POST("api/favorites/{kosId}")
    suspend fun addToFavorite(
        @Header("Authorization") token: String,
        @Path("kosId") kosId: Int
    ): Response<ApiResponse<Any>>
    
    @DELETE("api/favorites/{kosId}")
    suspend fun removeFromFavorite(
        @Header("Authorization") token: String,
        @Path("kosId") kosId: Int
    ): Response<ApiResponse<Any>>
    
    @GET("api/favorites")
    suspend fun getUserFavorites(@Header("Authorization") token: String): Response<ApiResponse<List<FavoriteKosDto>>>
    
    @GET("api/favorites/check/{kosId}")
    suspend fun checkFavoriteStatus(
        @Header("Authorization") token: String,
        @Path("kosId") kosId: Int
    ): Response<ApiResponse<FavoriteStatusResponse>>
}

