package com.example.kotlintodopractice

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import android.media.RingtoneManager

const val CHANNEL_ID = "NOTIFICATION_CHANNEL"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle FCM messages here
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if the message contains data
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        // Check if the message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Title: ${it.title}")
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        if(remoteMessage.notification !=null){
            Log.d(TAG, "Generating notification")
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
            Log.d(TAG, "Notification sent")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle new or refreshed FCM registration token
        Log.d(TAG, "Refreshed token: $token")
        // You may want to send this token to your server for further use
    }

    fun generateNotification(title: String, message: String){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = CHANNEL_ID
//        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notifaction)
                .setContentTitle(title)
                .setContentText(message)
                .setVibrate(longArrayOf(1000,1000,1000,1000))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        Log.d(TAG, "After notification manager")
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Todo App",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}