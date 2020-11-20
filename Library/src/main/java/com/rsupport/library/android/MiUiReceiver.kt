package com.rsupport.library.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.rsupport.library.XiaomiNotificationManager
import com.rsupport.library.android.MiuiTransparentActivity.Companion.EXTRA_MIUI_ACTIVITY_RECHECK
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MiUiReceiver(
    private val notificationManager: XiaomiNotificationManager,
    private val onGranted: () -> Unit = {},
    private val onDenied: () -> Unit = {}
) : BroadcastReceiver() {
    companion object {
        private const val INTENT_FILTER_MIUI = "miui.receiver.intent.filter"
        private const val EXTRA_RESULT_KEY = "miui.receiver.key"
        private const val EXTRA_RESULT_CHECK_OK = 3000
        private const val EXTRA_RESULT_RETRY_CHECK_OK = 3001
        private const val EXTRA_RESULT_OPENED_PERMISSION = 3002
        private const val EXTRA_HEAD_UP_NOTIFICATION_CANCEL = 4000

        fun sendResultCheck(context: Context?) = Intent(INTENT_FILTER_MIUI).apply {
            putExtra(
                EXTRA_RESULT_KEY,
                EXTRA_RESULT_CHECK_OK
            )
        }.run {
            context?.sendBroadcast(this)
        }

        fun sendResultRetryCheck(context: Context?) = Intent(INTENT_FILTER_MIUI).apply {
            putExtra(
                EXTRA_RESULT_KEY,
                EXTRA_RESULT_RETRY_CHECK_OK
            )
        }.run {
            context?.sendBroadcast(this)
        }

        fun sendResultOpenedPermission(context: Context?) = Intent(INTENT_FILTER_MIUI).apply {
            putExtra(
                EXTRA_RESULT_KEY,
                EXTRA_RESULT_OPENED_PERMISSION
            )
        }.run {
            context?.sendBroadcast(this)
        }

        fun getDeleteIntent() = Intent(INTENT_FILTER_MIUI).apply {
            putExtra(
                EXTRA_RESULT_KEY,
                EXTRA_HEAD_UP_NOTIFICATION_CANCEL
            )
        }
    }

    private val latch = CountDownLatch(1)
    private val retryCheckLatch = CountDownLatch(1)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == INTENT_FILTER_MIUI) {
            when (intent.getIntExtra(EXTRA_RESULT_KEY, 0)) {
                EXTRA_HEAD_UP_NOTIFICATION_CANCEL -> {
                    unregister(context)
                    onDenied.invoke()
                }
                EXTRA_RESULT_CHECK_OK -> {
                    latch.countDown()
                    unregister(context)
                    onGranted.invoke()
                }
                EXTRA_RESULT_OPENED_PERMISSION -> {
                    startCheckActivity(context, EXTRA_MIUI_ACTIVITY_RECHECK)
                    retryCheckAwaitLatch(context)
                }

                EXTRA_RESULT_RETRY_CHECK_OK -> {
                    retryCheckLatch.countDown()
                    unregister(context)
                    onGranted.invoke()
                }
                else -> return
            }
            notificationManager.stop()
        }
    }

    fun awaitLatch() {
        if (!latch.await(3, TimeUnit.SECONDS)) {
            notificationManager.start()
        }
    }

    fun register(context: Context?) {
        context?.registerReceiver(this, IntentFilter(INTENT_FILTER_MIUI))
    }

    fun startCheckActivity(context: Context?, extra: Int) {
        context?.let {
            MiuiTransparentActivity.startActivity(
                context,
                extra
            )
        }
    }

    fun unregister(context: Context?) {
        context?.unregisterReceiver(this)
    }

    private fun retryCheckAwaitLatch(context: Context?) {
        Thread {
            if (!retryCheckLatch.await(3, TimeUnit.SECONDS)) {
                onDenied.invoke()
                unregister(context)
            }
        }.start()
    }
}