package com.rsupport.xiaomi

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rsupport.library.XiaomiBackgroundPermissionChecker

class MainActivity : AppCompatActivity() {

    private var isBound = false
    private lateinit var sampleService: SampleService

    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(component: ComponentName?) {
            isBound = false
        }

        override fun onServiceConnected(component: ComponentName?, binder: IBinder?) {
            sampleService = (binder as SampleService.SampleBinder).getService()
            isBound = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        Intent(this, SampleService::class.java).also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
        finish()
    }
}