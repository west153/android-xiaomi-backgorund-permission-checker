package com.rsupport.xiaomi

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.rsupport.library.support.rx.RxXiaomiPermission
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class SampleRxChecker(private val context: Context) {
    private val compositeDisposable = CompositeDisposable()
    val action = "sample.rx.checker"


    fun checkPermission() {
        RxXiaomiPermission.request(context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it) {
                    Toast.makeText(context, "permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show()
                }
            }, {
                it.printStackTrace()
            })
            .addTo(compositeDisposable)
    }

    fun clear() {
        compositeDisposable.clear()
    }

    fun getPendingIntent() =
        PendingIntent.getService(context, 0, createIntent(), PendingIntent.FLAG_UPDATE_CURRENT)

    private fun createIntent() = Intent(context, SampleService::class.java).apply {
        this.action = this@SampleRxChecker.action
    }
}