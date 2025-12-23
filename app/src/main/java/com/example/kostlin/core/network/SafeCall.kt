package com.example.kostlin.core.network

import com.example.kostlin.data.remote.dto.BaseResponse
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> executeRequest(
    defaultValue: T? = null,
    call: suspend () -> BaseResponse<T>
): ApiResult<T> {
    return try {
        val response = call.invoke()
        if (response.success) {
            val data = response.data ?: defaultValue
            if (data != null) {
                ApiResult.success(data)
            } else {
                // Success response but no data - return success with message
                @Suppress("UNCHECKED_CAST")
                ApiResult.success(Unit as T)
            }
        } else {
            ApiResult.error(response.message ?: "Terjadi kesalahan pada server")
        }
    } catch (exception: HttpException) {
        val errorBody = exception.response()?.errorBody()?.string()
        val message = try {
            // Parse JSON error response to extract message
            errorBody?.let {
                val json = JSONObject(it)
                json.optString("message", null)
            }
        } catch (e: Exception) {
            null
        } ?: exception.message() ?: "Permintaan gagal"
        ApiResult.error(message, code = exception.code(), throwable = exception)
    } catch (exception: IOException) {
        ApiResult.error("Tidak dapat terhubung ke server", throwable = exception)
    } catch (exception: Exception) {
        ApiResult.error(exception.message ?: "Kesalahan tidak diketahui", throwable = exception)
    }
}
