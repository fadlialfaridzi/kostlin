package com.example.kostlin.core.network

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(
        val code: Int? = null,
        val message: String,
        val throwable: Throwable? = null
    ) : ApiResult<Nothing>()

    companion object {
        fun <T> success(data: T): ApiResult<T> = Success(data)
        fun error(message: String, code: Int? = null, throwable: Throwable? = null): ApiResult.Error =
            Error(code = code, message = message, throwable = throwable)
    }
}

inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> = when (this) {
    is ApiResult.Success -> ApiResult.success(transform(data))
    is ApiResult.Error -> this
}

inline fun <T> ApiResult<T>.onSuccess(block: (T) -> Unit): ApiResult<T> = apply {
    if (this is ApiResult.Success) {
        block(data)
    }
}

inline fun <T> ApiResult<T>.onError(block: (ApiResult.Error) -> Unit): ApiResult<T> = apply {
    if (this is ApiResult.Error) {
        block(this)
    }
}
