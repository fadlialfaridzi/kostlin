package com.example.kostlin.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.kostlin.MainActivity
import com.example.kostlin.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class KostlinFirebaseMessagingService : FirebaseMessagingService() {
    
    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "kostlin_notifications"
        private const val CHANNEL_NAME = "Kostlin Notifications"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "FCM Service created and running!")
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token: $token")
        // Save token to SharedPreferences for later use
        saveTokenToPrefs(token)
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "============ FCM MESSAGE RECEIVED ============")
        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Message ID: ${remoteMessage.messageId}")
        Log.d(TAG, "Data: ${remoteMessage.data}")
        Log.d(TAG, "Notification: ${remoteMessage.notification?.title} - ${remoteMessage.notification?.body}")
        
        // Extract title and body - prioritize notification payload, fallback to data payload
        val title: String
        val body: String
        
        if (remoteMessage.notification != null) {
            // Use notification payload if available
            title = remoteMessage.notification?.title ?: "Kostlin"
            body = remoteMessage.notification?.body ?: ""
        } else if (remoteMessage.data.isNotEmpty()) {
            // Fallback to data payload
            title = remoteMessage.data["title"] ?: "Kostlin"
            body = remoteMessage.data["body"] ?: ""
        } else {
            Log.d(TAG, "No payload to process")
            return
        }
        
        val bookingId = remoteMessage.data["bookingId"]
        showNotification(title, body, bookingId)
        
        Log.d(TAG, "============================================")
    }
    
    private fun showNotification(title: String, body: String, bookingId: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi booking kos"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create intent to open app with booking detail
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "booking_detail")
            bookingId?.let { putExtra("booking_id", it) }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
    
    private fun saveTokenToPrefs(token: String) {
        val prefs = getSharedPreferences("kostlin_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
    }
}
