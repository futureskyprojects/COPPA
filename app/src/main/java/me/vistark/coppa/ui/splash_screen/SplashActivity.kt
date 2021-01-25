package me.vistark.coppa.ui.splash_screen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vistark.coppa.R
import me.vistark.coppa._core.api.APIService
import me.vistark.coppa.application.DefaultValue
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.ui.auth.AuthActivity
import me.vistark.coppa.ui.home.HomeActivity
import me.vistark.fastdroid.core.api.JwtAuth.isAuthenticated
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.utils.PermissionUtils.requestAllPermissions
import me.vistark.fastdroid.utils.GlideUtils.load
import me.vistark.fastdroid.utils.InternetUtils.isInternetAvailable
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.TimerUtils.startAfter
import retrofit2.await
import java.lang.Exception
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
        // Nếu không có mạng thì tiến hành chuyển màn hình luôn
        if (!isInternetAvailable()) {
            startNext()
            return
        }

        // Nếu không tiến hành cập nhật các dữ liệu từ server
        GlobalScope.launch {
            try {
                val apis = APIService().APIs
                // Lấy danh sách các nhóm loài
                val speciesCategory = apis.getSpeciesCategories().await()
                speciesCategory.result?.apply {
                    RuntimeStorage.SpeciesCategories = this.toTypedArray()
                }

                // Lấy danh sách các loài
                val specieses = apis.getSpecieses().await()
                specieses.result?.apply {
                    RuntimeStorage.Specieses = this.toTypedArray()
                }

                // Lấy danh sách các cảng biển
                val seaPorts = apis.getSeaPorts().await()
                seaPorts.result?.apply {
                    RuntimeStorage.SeaPorts = this.toTypedArray()
                }

                // Lấy danh sách lịch sử chuyến đi biển
                val tripLogs = apis.getTripHistories().await()
                tripLogs.result?.apply {
                    RuntimeStorage.TripLogs = this.toTypedArray()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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