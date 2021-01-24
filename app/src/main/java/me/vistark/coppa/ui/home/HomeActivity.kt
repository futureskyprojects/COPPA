package me.vistark.coppa.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.marginLeft
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.layout_control_component_in_trip.*
import kotlinx.android.synthetic.main.layout_control_component_out_trip.*
import kotlinx.android.synthetic.main.layout_home_header_component.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vistark.coppa.R
import me.vistark.coppa._core.api.APIService
import me.vistark.coppa._core.utils.CorrectURL.correctPath
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.services.BackgroundService
import me.vistark.fastdroid.core.models.FastdroidCoordinate
import me.vistark.fastdroid.services.FastdroidService.Companion.isServiceRunning
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.ui.overlay.LoadingBase.showLoadingBase
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownBottomLeft
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownBottomRight
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownCenter
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpBottomLeft
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpBottomRight
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpCenter
import me.vistark.fastdroid.utils.GlideUtils.loadImage
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.PermissionUtils.isPermissionGranted
import me.vistark.fastdroid.utils.ViewExtension.onTap
import me.vistark.fastdroid.utils.ViewExtension.setMargin
import retrofit2.await
import kotlin.math.max

class HomeActivity : FastdroidActivity(R.layout.activity_home, isLimit = false),
    OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Load user nếu đã có
        loadCurrentUserInfo()

        // Điều chỉnh vị trí của thanh tiêu đề
        changeHeaderBarPosition()

        // Thiết đặt hiệu ứng khi nhấn các nút
        initViewAnimations()

        // Lấy profile của người dùng
        getUserProfileProcessing()

        // Khởi động services
        runServices()

        // Init loading map dialog
        initLoadingMapDialog()
    }

    private fun initLoadingMapDialog() {
        ahLnLoadingSmallDialog.scaleUpCenter(200)
        aloTvLoadingMessage.text = L("SystemIsGettingYourLocation")
        aloIvLoadingOverlayIcon.loadImage(me.vistark.fastdroid.R.raw.loading_pink)
    }


    private fun getUserProfileProcessing() {
        val loading = showLoadingBase()
        GlobalScope.launch {
            try {
                val res = APIService().APIs.getCaptainProfile().await()
                if (res.status == 200 && res.result != null) {
                    // Thông báo thành công
                    runOnUiThread {
                        loadCurrentUserInfo()
                    }
                    RuntimeStorage.CurrentCaptainProfile = res.result!!
                } else {
                    if (res.message.isNotEmpty()) {
                        Toasty.error(
                            this@HomeActivity.applicationContext,
                            res.message.first(),
                            Toasty.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toasty.error(
                        this@HomeActivity.applicationContext,
                        L("UnexptectedError"),
                        Toasty.LENGTH_SHORT,
                        true
                    ).show()
                }
            } finally {
                runOnUiThread {
                    loading.dismiss()
                }
            }
        }
    }

    // Hiển thị thông tin của thuyền trưởng hiện tại
    private fun loadCurrentUserInfo() {
        if (RuntimeStorage.CurrentCaptainProfile.captain.isNotEmpty()) {
            lhhcTvWelcomeText.text =
                String.format(
                    L("HiCaptain%s!"),
                    RuntimeStorage.CurrentCaptainProfile.captain.split(" ").lastOrNull() ?: ""
                )
        } else {
            lhhcTvWelcomeText.text =
                String.format(
                    L("HiCaptain!")
                )
        }

        if (RuntimeStorage.CurrentCaptainProfile.image.isEmpty()) {
            lhhcIvUserAvatar.loadImage(RuntimeStorage.CurrentCaptainProfile.image.correctPath())
        }
    }

    private fun initViewAnimations() {
        // Khi nhấn nút tạo chuyến đi biển mới
        lccotBtnCreateNewTrip.onTap {
            onCreateTrip()
            lccotLnOutTripLayout.scaleDownBottomRight()
            lccotLnInTripLayout.scaleUpBottomLeft()
        }

        // Khi nhấn nút kết thúc chuyến đi biển
        lccitBtnEndTrip.onTap {
            onEnTrip()
            lccotLnOutTripLayout.scaleUpBottomRight()
            lccotLnInTripLayout.scaleDownBottomLeft()
        }

        // Khi nhấn vào ảnh đại diện của người dùng
        lhhcIvUserAvatar.onTap {

        }
    }

    private fun onEnTrip() {

    }

    private fun onCreateTrip() {

    }

    private fun changeHeaderBarPosition() {
        onStatusbarHeight {
            if (statusBarHeight > 0) {
//                println(">>>>>>>>>>>>>>> Height: $it")
                ahCvTopHeaderbarLayout.setMargin(
                    top = max(
                        ahCvTopHeaderbarLayout.marginLeft,
                        it
                    ) + ahCvTopHeaderbarLayout.marginLeft / 2
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (isPermissionGranted(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            mMap.isMyLocationEnabled = true
        }
    }

    private fun MoveCamera(lat: Double, lng: Double) {
        if (ahLnLoadingSmallDialog.visibility != View.GONE)
            ahLnLoadingSmallDialog.scaleDownCenter(200)
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    lat,
                    lng
                ), 12.0f
            )
        )
    }

    override fun onSignal(key: String, value: String) {
        if (key == BackgroundService.REALTIME_COORDINATES) {
            println(">>>>>>>>>>>>>>>>>>>>>>>> $value")
            val data = Gson().fromJson(value, FastdroidCoordinate::class.java)
            MoveCamera(data.lat, data.long)
        }
    }

    private fun runServices() {
        if (!isServiceRunning(BackgroundService::class.java.name)) {
            val intent = Intent(this, BackgroundService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }
}