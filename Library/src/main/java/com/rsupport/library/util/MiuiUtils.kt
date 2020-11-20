package com.rsupport.library.util

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object MiuiUtils {
    fun isMiuiDevice() = getSystemProperty("ro.miui.ui.version.name")?.isNotEmpty() == true

    private fun getSystemProperty(propName: String): String? = try {
        Runtime.getRuntime().exec("getprop $propName").let { process ->
            BufferedReader(InputStreamReader(process.inputStream), 1024)
        }.use {
            it.readLine()
        }
    } catch (e: IOException) {
        Log.e(this::class.java.name, e.message ?: "")
        null
    }
}