package com.example.receive_fcm_kotlin.java

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.receive_fcm_kotlin.MainActivity
import com.example.receive_fcm_kotlin.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // ponytail: minimal foreground handler. FCM auto-shows notification msgs in background; this covers foreground + data msgs.
        Log.d("FCM", "onMessageReceived: data=${message.data} notification=${message.notification}")
        val title = message.notification?.title ?: message.data["title"] ?: "FCM Message"
        val body = message.notification?.body ?: message.data["body"] ?: message.data.toString().ifEmpty { "No content" }
        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "fcm_default_channel"
        val nm = getSystemService(NotificationManager::class.java)
        // ponytail: HIGH for heads-up banner when phone is in use; recreate if channel existed as DEFAULT (needs reinstall/clear or delete)
        var channel = nm.getNotificationChannel(channelId)
        if (channel == null || channel.importance < NotificationManager.IMPORTANCE_HIGH) {
            if (channel != null) nm.deleteNotificationChannel(channelId)
            channel = NotificationChannel(channelId, "FCM", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "FCM high-priority"
                enableVibration(true)
            }
            nm.createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt() // avoid collapse_key overwriting
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        nm.notify(id, notification)
    }
}
