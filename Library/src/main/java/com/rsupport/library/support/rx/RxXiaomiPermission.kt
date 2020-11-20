package com.rsupport.library.support.rx

import android.content.Context
import com.rsupport.library.XiaomiBackgroundPermissionChecker
import io.reactivex.Single

object RxXiaomiPermission {
    fun request(context: Context) = Single.create<Boolean> { emitter ->
        XiaomiBackgroundPermissionChecker()
            .check(context, {
                emitter.onSuccess(true)
            }, {
                emitter.onSuccess(false)
            })
    }
}