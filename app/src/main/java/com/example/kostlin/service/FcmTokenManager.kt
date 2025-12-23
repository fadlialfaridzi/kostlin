package com.example.kostlin.service

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Manages Firebase Cloud Messaging token operations
 */
object FcmTokenManager {
    private const val TAG = "FcmTokenManager"
    private const val PREFS_NAME = "kostlin_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"
    
    /**
     * Get FCM token asynchronously using await
     */
    suspend fun getToken(): String? {
        return try {
            Log.d(TAG, "Attempting to get FCM token...")
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "FCM token retrieved successfully: ${token.take(20)}...")
            token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get FCM token using await, trying callback method", e)
            // Fallback to callback method
            getTokenWithCallback()
        }
    }
    
    /**
     * Get FCM token using callback approach (fallback)
     */
    private suspend fun getTokenWithCallback(): String? = suspendCancellableCoroutine { continuation ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(TAG, "Fetching FCM registration token failed", task.exception)
                continuation.resume(null)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d(TAG, "FCM token from callback: ${token?.take(20)}...")
            continuation.resume(token)
        }
    }
    
    /**
     * Get saved token from SharedPreferences
     */
    fun getSavedToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FCM_TOKEN, null)
    }
    
    /**
     * Save token to SharedPreferences
     */
    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
        Log.d(TAG, "FCM token saved to prefs")
    }
    
    /**
     * Clear saved token (on logout)
     */
    fun clearToken(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_FCM_TOKEN).apply()
    }
}

