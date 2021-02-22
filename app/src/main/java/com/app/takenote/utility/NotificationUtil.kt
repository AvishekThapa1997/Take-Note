package com.app.takenote.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.takenote.R
import com.app.takenote.ui.SplashActivity

private const val CHANNEL_ID = "Reminder"
private const val NOTIFICATION_ID = 100
fun showNotification(context: Context, title: String, body: String) {
    val intent = Intent(context, SplashActivity::class.java)
//        .apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    //TaskStackBuilder.create(context).run {
//        // Add the intent, which inflates the back stack
//        addNextIntent()
//        addNextIntentWithParentStack(intent)
//        // Get the PendingIntent containing the entire back stack
//        getPendingIntent(0, PendingIntent.F)
//    }
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.applogo)
        .setContentTitle(title)
        .setContentText(body)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(body)
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
    createNotificationChannel(context)
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
}

private fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
