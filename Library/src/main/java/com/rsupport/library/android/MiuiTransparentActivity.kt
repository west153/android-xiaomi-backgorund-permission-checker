package com.rsupport.library.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class MiuiTransparentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when {
            intent.getStringExtra(INTENT_PENDING_MIUI) != null -> {
                openXiaomiPermissionEditor()
            }
            intent.getIntExtra(EXTRA_MIUI_ACTIVITY_KEY, -1) == EXTRA_MIUI_ACTIVITY_CHECK -> {
                MiUiReceiver.sendResultCheck(this)
                finish()
            }
            intent.getIntExtra(EXTRA_MIUI_ACTIVITY_KEY, -1) == EXTRA_MIUI_ACTIVITY_RECHECK -> {
                MiUiReceiver.sendResultRetryCheck(this)
                finish()
            }
            else -> finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDITOR_ACTIVITY_RESULT_CODE && resultCode == Activity.RESULT_CANCELED) {
            finish()
            // 바로 체크 activity 를 시작하게되면 아직 background 상태로 돌아오지않았기때문에, 권한이없어도 체크 activity 를 시작할수가있음.
            // background 상태로 돌아올떄까지 임의로 딜레이 발생.
            Handler().postDelayed({
                MiUiReceiver.sendResultOpenedPermission(applicationContext)
            }, 500)
        }
    }

    private fun openXiaomiPermissionEditor() {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
        )
        intent.putExtra("extra_pkgname", packageName)
        startActivityForResult(intent,
            EDITOR_ACTIVITY_RESULT_CODE
        )
    }

    companion object {
        private const val EDITOR_ACTIVITY_RESULT_CODE = 1004
        private const val EXTRA_MIUI_ACTIVITY_KEY = "miui.activity"
        private const val INTENT_PENDING_MIUI = "INTENT_PENDING_MIUI"

        const val EXTRA_MIUI_ACTIVITY_CHECK = 0
        const val EXTRA_MIUI_ACTIVITY_RECHECK = 1

        fun startActivity(context: Context, extra: Int) = Intent(context, MiuiTransparentActivity::class.java).run {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(EXTRA_MIUI_ACTIVITY_KEY, extra)
            context.startActivity(this)
        }

        fun createMiuiIntent(context: Context) = Intent(context, MiuiTransparentActivity::class.java).apply {
            putExtra(INTENT_PENDING_MIUI, "value")
        }
    }

}