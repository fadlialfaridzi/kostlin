package com.example.kostlin.data.api.model

import com.google.gson.annotations.SerializedName

// Base API Response
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: T? = null,
    @SerializedName("error")
    val error: String? = null,
    @SerializedName("count")
    val count: Int? = null
)

// Auth Models
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class RegisterRequest(
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class AuthResponse(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("token")
    val token: String
)

data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String
)

data class VerifyOTPRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("code")
    val code: String
)

data class ResetPasswordRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class UserProfile(
    @SerializedName("id")
    val id: Int,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("is_verified")
    val isVerified: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String? = null
)

// Kos Models
data class KosFacilityDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("isAvailable")
    val isAvailable: Boolean = true
)

data class KosReviewDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userAvatar")
    val userAvatar: String? = null,
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("isVerified")
    val isVerified: Boolean = false
)

data class KosPropertyDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("price")
    val price: String? = null,
    @SerializedName("pricePerMonth")
    val pricePerMonth: Int? = null,
    @SerializedName("rating")
    val rating: String? = null,
    @SerializedName("ratingValue")
    val ratingValue: Float? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("facilities")
    val facilities: List<String>? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("isPopular")
    val isPopular: Boolean = false,
    @SerializedName("isRecommended")
    val isRecommended: Boolean = false,
    @SerializedName("ownerEmail")
    val ownerEmail: String? = null,
    @SerializedName("ownerPhone")
    val ownerPhone: String? = null,
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null,
    @SerializedName("reviewCount")
    val reviewCount: Int = 0,
    @SerializedName("reviews")
    val reviews: List<KosReviewDto>? = null
)

// Favorite Models
data class FavoriteKosDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("kosProperty")
    val kosProperty: KosPropertyDto,
    @SerializedName("dateAdded")
    val dateAdded: String,
    @SerializedName("status")
    val status: String
)

data class FavoriteStatusResponse(
    @SerializedName("isFavorite")
    val isFavorite: Boolean
)

// Create Kos Request
data class CreateKosRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("pricePerMonth")
    val pricePerMonth: Int,
    @SerializedName("type")
    val type: String, // PUTRA, PUTRI, CAMPUR
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("ownerEmail")
    val ownerEmail: String? = null,
    @SerializedName("ownerPhone")
    val ownerPhone: String? = null,
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null,
    @SerializedName("facilities")
    val facilities: List<String> = emptyList(),
    @SerializedName("imageUrl")
    val imageUrl: String? = null
)

