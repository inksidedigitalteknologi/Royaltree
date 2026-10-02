package com.inkside.digital.data.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.inkside.digital.MainActivity
import com.inkside.digital.R

class RoyaltreeFcmService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "RoyaltreeFCM"
        private const val CHANNEL_ID = "royaltree_default"
        private const val CHANNEL_NAME = "Royaltree Notifications"
    }

    @Suppress("DEPRECATION")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: ${token.take(20)}...")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM message received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val type = data["type"] ?: "GENERAL"
        val title = remoteMessage.notification?.title
            ?: data["title"]
            ?: "Royaltree"
        val body = remoteMessage.notification?.body
            ?: data["body"]
            ?: ""

        Log.d(TAG, "Notification type: $type, title: $title, body: $body")

        showNotification(title, body, type, data)
    }

    private fun showNotification(
        title: String,
        body: String,
        type: String,
        data: Map<String, String>
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data.forEach { (k, v) -> putExtra(k, v) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Royaltree"
                enableLights(true)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
