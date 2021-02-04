package me.vistark.coppa.ui.splash_screen

import android.os.Bundle
import android.widget.Toast
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_splash.*
import me.vistark.coppa.R
import me.vistark.coppa._core.remote_config.RemoteConfig.initRemoteConfig
import me.vistark.coppa.application.DefaultValue
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.application.RuntimeStorage.SavedCulture
import me.vistark.coppa.application.RuntimeStorage.cacheImages
import me.vistark.coppa.application.RuntimeStorage.init
import me.vistark.coppa.domain.entity.languages.CoppaTrans.Companion.syncLanguage
import me.vistark.coppa.ui.auth.AuthActivity
import me.vistark.coppa.ui.home.HomeActivity
import me.vistark.fastdroid.core.api.JwtAuth.isAuthenticated
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.utils.GlideUtils.load
import me.vistark.fastdroid.utils.InternetUtils.isInternetAvailable
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.PermissionUtils.requestAllPermissions
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
        aloIvLoadingOverlayIcon.load(R.raw.loading_pink)
        appStartAtMillis = System.currentTimeMillis()

        initRemoteConfig() {
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
    }

    private fun startInitDataForApp() {
        // Nếu không có mạng thì tiến hành chuyển màn hình luôn
        if (!isInternetAvailable()) {
            startNext()
            return
        }
        syncLanguage()

        // Nếu đã đăng nhập, tiến hành sync data từ server
        if (isAuthenticated()) {
            init {
                aloTvLoadingMessage.post {
                    aloTvLoadingMessage.text = L(getString(R.string.CachingAppImagesData))
                }
                cacheImages {
                    runOnUiThread {
                        startNext()
                    }
                }
            }
        } else {
            runOnUiThread {
                startNext()
            }
        }
    }

    private fun startNext() {
        startAfter(max(1500 - (System.currentTimeMillis() - appStartAtMillis), 1500)) {
            if (isAuthenticated()) {
                startSingleActivity(HomeActivity::class.java)
            } else {
                startSingleActivity(AuthActivity::class.java)
            }
        }
    }

}