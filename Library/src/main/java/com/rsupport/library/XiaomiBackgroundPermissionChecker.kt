package com.rsupport.library

import android.content.Context
import android.util.Log
import com.rsupport.library.android.MiUiReceiver
import com.rsupport.library.android.MiuiTransparentActivity.Companion.EXTRA_MIUI_ACTIVITY_CHECK
import com.rsupport.library.util.MiuiUtils

class XiaomiBackgroundPermissionChecker {

    fun check(
        context: Context,
        onGranted: () -> Unit = {},
        onDenied: () -> Unit = {}
    ): Unit = synchronized(this) {
        if (!MiuiUtils.isMiuiDevice()) return onGranted.invoke()
        try {
            val notificationManager = XiaomiNotificationManager(context)
            MiUiReceiver(notificationManager, onGranted, onDenied).also { receiver ->
                receiver.register(context)
                receiver.startCheckActivity(context, EXTRA_MIUI_ACTIVITY_CHECK)
                receiver.awaitLatch()
            }
        } catch (e: Exception) {
            Log.e(this::class.java.name, e.message ?: "")
            false
        }
    }
}