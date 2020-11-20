package com.rsupport.library

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.rsupport.library.android.MiUiReceiver
import com.rsupport.library.android.MiuiTransparentActivity
import com.rsupport.library.util.MiuiUtils

class XiaomiNotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationId = 1001
    private val channelId = "REMOTECALL7_XIAOMI_NOTIFICATION_CHANNEL_ID"
    private val name = "RemoteCall7 Xiaomi Foreground Service"

    init {
        initialize()
    }

    fun start() {
        NotificationManagerCompat.from(context).notify(notificationId, setupNotification())
    }

    fun stop() {
        notificationManager.cancel(notificationId)
    }

    fun deleteChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(
                channelId
            ) != null
        ) {
            notificationManager.deleteNotificationChannel(channelId)
        }
    }

    private fun initialize() {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && MiuiUtils.isMiuiDevice()) {
            if (notificationManager.getNotificationChannel(channelId) != null) return

            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setupNotification(): Notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(getIdSmallIcon())
        .setColor(
            ContextCompat.getColor(
                context,
                R.color.design_default_color_on_primary //TODO Input Color
            )
        )
        .setContentIntent(createContentIntent())
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOnlyAlertOnce(true)
        .setTimeoutAfter(15000)
        .setDeleteIntent(createDeleteIntent())
        .setContentTitle("xiaomi title") //TODO Input title
        .setContentText("xiaomi content") //TODO Input content
        .build()

    private fun getIdSmallIcon(): Int {
        //TODO Input icon
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            0
        } else {
            0
        }
    }

    private fun createContentIntent(): PendingIntent {
        val intent = MiuiTransparentActivity.createMiuiIntent(context)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createDeleteIntent(): PendingIntent {
        return PendingIntent.getBroadcast(
            context.applicationContext,
            0,
            MiUiReceiver.getDeleteIntent(),
            0
        )
    }

}