package me.vistark.coppa.ui.splash_screen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_splash.*
import me.vistark.coppa.R
import me.vistark.coppa.application.DefaultValue
import me.vistark.coppa.ui.auth.AuthActivity
import me.vistark.coppa.ui.home.HomeActivity
import me.vistark.fastdroid.core.api.JwtAuth.isAuthenticated
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.utils.PermissionUtils.requestAllPermissions
import me.vistark.fastdroid.utils.GlideUtils.loadImage
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.TimerUtils.startAfter
import kotlin.math.max

class SplashActivity : FastdroidActivity(
    R.layout.activity_splash,
    isLimit = false,
    windowBackground = R.drawable.bg_window_45
) {

    var appStartAtMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aloIvLoadingOverlayIcon.loadImage(R.raw.loading_pink)
        appStartAtMillis = System.currentTimeMillis()

        requestAllPermissions(
            DefaultValue.AppPermissions,
            L(getString(R.string.RequestPermission)),
            onCompleted = {
                startInitDataForApp()
            }, onDenied = {
                Toasty.error(
                    this,
                    L(getString(R.string.AppRequiredAcceptAllPermissionsToMakeActive)),
                    Toast.LENGTH_SHORT,
                    true
                ).show()
                startAfter(300) {
                    finish()
                }
            })
    }

    private fun startInitDataForApp() {
        startNext()
    }

    fun startNext() {
        startAfter(max(System.currentTimeMillis() - appStartAtMillis, 1500)) {
            if (isAuthenticated()) {
                val intent = Intent(this, HomeActivity::class.java)
                startSingleActivity(intent)
            } else {
                val intent = Intent(this, AuthActivity::class.java)
                startSingleActivity(intent)
            }
        }
    }

}