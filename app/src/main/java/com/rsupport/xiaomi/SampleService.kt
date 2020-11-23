package com.rsupport.xiaomi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SampleService : Service() {
    private lateinit var binder: SampleBinder
    private lateinit var notificationManager: NotificationManager
    private lateinit var sampleChecker: SampleChecker
    private lateinit var sampleRxChecker: SampleRxChecker
    private val stopAction = "service.stop"

    override fun onCreate() {
        super.onCreate()
        binder = SampleBinder()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        sampleChecker = SampleChecker(this)
        sampleRxChecker = SampleRxChecker(this)
        createNotificationChannel()

        notificationManager.notify(111, showNotification())
    }


    override fun onDestroy() {
        super.onDestroy()
        sampleRxChecker.clear()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            sampleChecker.action -> sampleChecker.checkPermission()
            sampleRxChecker.action -> sampleRxChecker.checkPermission()
            stopAction -> {
                stopSelf()
                notificationManager.cancel(111)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    inner class SampleBinder : Binder() {
        fun getService(): SampleService = this@SampleService
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                "sample.xiaomi.permission",
                "SampleXiaomiPermission",
                NotificationManager.IMPORTANCE_DEFAULT
            ).also {
                notificationManager.createNotificationChannel(it)
            }
        }
    }

    private fun showNotification() = NotificationCompat.Builder(this, "sample.xiaomi.permission")
        .setCustomContentView(createRemoteViews())
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.design_default_color_on_primary
            )
        )
        .setSmallIcon(R.mipmap.ic_launcher)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOnlyAlertOnce(true)
        .build()

    private fun createRemoteViews() =
        RemoteViews(this.packageName, R.layout.notification_sample).also {
            it.setOnClickPendingIntent(R.id.default_check, sampleChecker.getPendingIntent())
            it.setOnClickPendingIntent(R.id.rx_check, sampleRxChecker.getPendingIntent())
            it.setOnClickPendingIntent(
                R.id.stop,
                PendingIntent.getService(
                    this, 0,
                    Intent(this, this::class.java).apply {
                        action = stopAction
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
}