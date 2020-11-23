package com.rsupport.xiaomi

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.rsupport.library.XiaomiBackgroundPermissionChecker

class SampleChecker(private val context: Context) {
    val action = "sample.default.checker"

    fun checkPermission() {
        Thread {
            XiaomiBackgroundPermissionChecker().check(context, onGranted = {
                Toast.makeText(context, "permission granted", Toast.LENGTH_SHORT).show()
            }, onDenied = {
                Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show()
            }
            )
        }.start()
    }

    fun getPendingIntent() =
        PendingIntent.getService(context, 0, createIntent(), PendingIntent.FLAG_UPDATE_CURRENT)

    private fun createIntent() = Intent(context, SampleService::class.java).apply {
        this.action = this@SampleChecker.action
    }
}