package com.example.kostlin.data.remote.dto

data class BaseResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)
