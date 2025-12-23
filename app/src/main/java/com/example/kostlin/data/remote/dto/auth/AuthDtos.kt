package com.example.kostlin.data.remote.dto.auth

import com.squareup.moshi.Json

data class LoginRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

data class RegisterRequestDto(
    @Json(name = "fullName") val fullName: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "phoneNumber") val phoneNumber: String
)

data class RequestResetPasswordDto(
    @Json(name = "email") val email: String
)

data class VerifyOtpDto(
    @Json(name = "email") val email: String,
    @Json(name = "otp") val otp: String
)

data class ResetPasswordDto(
    @Json(name = "email") val email: String,
    @Json(name = "otp") val otp: String,
    @Json(name = "newPassword") val newPassword: String
)

data class TokenPairDto(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String
)

data class UserDto(
    @Json(name = "id") val id: Int,
    @Json(name = "fullName") val fullName: String,
    @Json(name = "email") val email: String,
    @Json(name = "phoneNumber") val phoneNumber: String?
)

data class AuthResponseDto(
    @Json(name = "user") val user: UserDto,
    @Json(name = "tokens") val tokens: TokenPairDto
)
