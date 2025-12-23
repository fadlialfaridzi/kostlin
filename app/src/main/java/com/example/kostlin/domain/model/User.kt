package com.example.kostlin.domain.model

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String?,
    val token: String? = null,
    val refreshToken: String? = null
)
