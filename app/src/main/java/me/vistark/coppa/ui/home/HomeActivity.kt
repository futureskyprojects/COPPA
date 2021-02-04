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
import kotlinx.android.synthetic.main.layout_home_control_component.*
import kotlinx.android.synthetic.main.layout_home_header_component.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vistark.coppa.R
import me.vistark.coppa._core.api.APIService
import me.vistark.coppa._core.utils.CorrectURL.coppaCorrectResourcePath
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.domain.entity.TripSync.Companion.createTripSync
import me.vistark.coppa.domain.entity.TripSync.Companion.endTripSync
import me.vistark.coppa.services.BackgroundService
import me.vistark.coppa.ui.category.CategoryActivity
import me.vistark.coppa.ui.profile_update.ProfileUpdateActivity
import me.vistark.coppa.ui.seaport.SeaPortActivity.Companion.StartPickSeaPort
import me.vistark.coppa.ui.seaport.SeaPortActivity.Companion.onPickSeaPortResultProcessing
import me.vistark.coppa.ui.species_sync_review.SpeciesSyncReviewActivity
import me.vistark.coppa.ui.trip_hostory.TripHistoryActivity
import me.vistark.fastdroid.core.models.FastdroidCoordinate
import me.vistark.fastdroid.services.FastdroidService.Companion.isServiceRunning
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.ui.dialog.PhotoViewDialog.bindZoomView
import me.vistark.fastdroid.ui.overlay.LoadingBase.showLoadingBase
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownBottomLeft
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownBottomRight
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownCenter
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpBottomLeft
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpBottomRight
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpCenter
import me.vistark.fastdroid.utils.GlideUtils.load
import me.vistark.fastdroid.utils.InternetUtils.isInternetAvailable
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.PermissionUtils.isPermissionGranted
import me.vistark.fastdroid.utils.ViewExtension.onTap
import me.vistark.fastdroid.utils.ViewExtension.setMargin
import me.vistark.fastdroid.utils.locations.LocationUtils.LatLngToDMS
import me.vistark.fastdroid.utils.Retrofit2Extension.Companion.await
import java.util.*
import kotlin.math.max

class HomeActivity : FastdroidActivity(
    R.layout.activity_home,
    isLimit = false,
    isCanAutoTranslate = true
),
    OnMapReadyCallback {

    companion object {
        var PUBLIC_CURRENT_COORDINATES: LatLng? = null
    }

    private val UPDATE_PROFILE_REQUEST_CODE = 11221

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Điều chỉnh vị trí của thanh tiêu đề
        changeHeaderBarPosition()

        // Thiết đặt hiệu ứng khi nhấn các nút
        initViewEventsAndAnimations()

        // Hiển thị control panel cho đúng trạng thái
        initControlPanelViews()

        // Lấy profile của người dùng nếu có mạng
        syncUserProfileProcessing()

        // Lấy các thông tin bị thiếu nếu chưa có
        startMissedDataAfterLoggedIn()

        // Khởi động services
        runServices()

        // Init loading map dialog
        initLoadingMapDialog()
    }

    private fun initControlPanelViews() {
        controlPanelBottomSpace.layoutParams.height = navigationBarHeight
        if (RuntimeStorage.CurrentTripSync != null) {
            showInTrip()
        } else {
            showOutTrip()
        }
    }

    private fun initLoadingMapDialog() {
        ahLnLoadingSmallDialog.scaleUpCenter(200)
        aloTvLoadingMessage.text = L(getString(R.string.SystemIsGettingYourLocation))
        aloIvLoadingOverlayIcon.load(me.vistark.fastdroid.R.raw.loading_pink)

        // Tạm khóa nút thêm loài do vị trí không khả dụng
        lccitBtnAddSpecies.isEnabled = false

    }


    private fun syncUserProfileProcessing() {
        // Load user nếu đã có
        loadCurrentUserInfo()

        // Nếu không có mạng thì bỏ qua
        if (!isInternetAvailable())
            return

        val loading = showLoadingBase()
        GlobalScope.launch {
            try {
                val res = APIService().APIs.getCaptainProfile().await()
                if (res!!.status == 200 && res.result != null) {
                    // Thông báo thành công
                    runOnUiThread {
                        res.result?.apply {
                            RuntimeStorage.CurrentCaptainProfile = this
                        }
                        loadCurrentUserInfo()
                    }
                } else {
                    if (res.message.isNotEmpty()) {
                        runOnUiThread {
                            Toasty.error(
                                this@HomeActivity.applicationContext,
                                res.message.first(),
                                Toasty.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toasty.error(
                        this@HomeActivity.applicationContext,
                        L(getString(R.string.UnxptectedError)),
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
        lhhcTvWelcomeText.text =
            String.format(
                L(getString(R.string.HiCaptain__s__)),
                RuntimeStorage.CurrentCaptainProfile.captain.split(" ").lastOrNull() ?: ""
            )
        lhhcTvWelcomeText.isSelected = true
        lhhcIvUserAvatar.load(
            RuntimeStorage.CurrentCaptainProfile.image.coppaCorrectResourcePath(),
            true
        )
    }

    private fun initViewEventsAndAnimations() {
        // Ban đầu sẽ ẩn view khi đang trong chuyến đi
        lccotLnInTripLayout.visibility = View.GONE
        // Khi nhấn nút tạo chuyến đi biển mới
        lccotBtnCreateNewTrip.onTap {
            // Chọn cảng xuất phát
            StartPickSeaPort(true)
        }

        // Khi nhấn nút kết thúc chuyến đi biển
        lccitBtnEndTrip.onTap {
            if (!isInternetAvailable()) {
                Toasty.warning(
                    this,
                    L(getString(R.string.YouMustConnectToInternetToHveFinishAndSyncCurrentTrip)),
                    Toasty.LENGTH_SHORT,
                    true
                ).show()
            } else {
                // Chọn cảng xuất phát
                StartPickSeaPort(false)
            }
        }

        // Khi nhấn vào ảnh đại diện của người dùng
        lhhcIvUserAvatar.onTap {
            // Khởi động màn hình cập nhật hồ sơ
            val intent = Intent(this, ProfileUpdateActivity::class.java)
            startActivityForResult(intent, UPDATE_PROFILE_REQUEST_CODE)
            overridePendingTransition(-1, -1)
        }

        lhhcIvUserAvatar.setOnLongClickListener {
            lhhcIvUserAvatar.bindZoomView(RuntimeStorage.CurrentCaptainProfile.image.coppaCorrectResourcePath())
            return@setOnLongClickListener true
        }

        // Khi nhán vào nút thêm loài bắt được vào chuyến
        lccitBtnAddSpecies.onTap {
            // Khoải động màn hình danh sách các nhóm loài
            startActivity(CategoryActivity::class.java)
            overridePendingTransition(-1, -1)
        }

        // Khi nhấn vào nút xem lại
        lccitBtnReview.onTap {
            // Khoải động màn hình danh sách các loài đã bắt
            startActivity(SpeciesSyncReviewActivity::class.java)
            overridePendingTransition(-1, -1)
        }

        //region Khi nhấn chọn lịch sử chuyến đi
        lccitBtnTripHistory.onTap {
            tripHistoryProcessing()
        }

        lccotBtnTripHistory.onTap {
            tripHistoryProcessing()
        }
        //endregion
    }

    fun tripHistoryProcessing() {
        // Khởi động màn hình danh sách lịch sử chuyến đi
        startActivity(TripHistoryActivity::class.java)
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
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isRotateGesturesEnabled = false
        if (isPermissionGranted(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            mMap.isMyLocationEnabled = true
        }
    }

    private fun MoveCamera(lat: Double, lng: Double) {
        if (ahLnLoadingSmallDialog.visibility != View.GONE) {
            ahLnLoadingSmallDialog.scaleDownCenter(200)
            lccitBtnAddSpecies.isEnabled = true
        }
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
            val data = Gson().fromJson(value, FastdroidCoordinate::class.java)
            lhhcTvCoordinates.text = LatLngToDMS(data.lat, data.long)
            lhhcTvCoordinates.isSelected = true
            PUBLIC_CURRENT_COORDINATES = LatLng(data.lat, data.long)
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

    private fun showInTrip() {
        lccotLnOutTripLayout.scaleDownBottomRight()
        lccotLnInTripLayout.scaleUpBottomLeft()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onPickSeaPortResultProcessing(
            requestCode,
            resultCode,
            data
        ) { seaPort, isSlectForStartSeaPort ->
            // Khi chọn cảng thành công
            if (isSlectForStartSeaPort) {
                // Khởi động chuyến đi
                createTripSync(seaPort.id)
                showInTrip()
            } else {
                // Khởi động chuyến về
                endTripSync(seaPort.id)
                showOutTrip()
            }
        }

        if (requestCode == UPDATE_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            syncUserProfileProcessing()
        }
    }

    private fun showOutTrip() {
        lccotLnOutTripLayout.scaleUpBottomRight()
        lccotLnInTripLayout.scaleDownBottomLeft()
    }

    private fun startMissedDataAfterLoggedIn() {
        // Nếu không có mạng thì tiến hành chuyển màn hình luôn
        if (!isInternetAvailable()) {
            return
        }

        val loading = showLoadingBase()
        // Nếu không tiến hành cập nhật các dữ liệu từ server
        GlobalScope.launch {
            try {
                val apis = APIService().APIs

                // Lấy danh sách các cảng biển nếu chưa có
                if (RuntimeStorage.SeaPorts.isEmpty()) {
                    val seaPorts = apis.getSeaPorts().await()
                    seaPorts!!.result?.apply {
                        RuntimeStorage.SeaPorts = this.toTypedArray()
                    }
                }

                // Lấy danh sách lịch sử chuyến đi biển
                val tripLogs = apis.getTripHistories().await()
                tripLogs!!.result?.apply {
                    RuntimeStorage.TripLogs = this.toTypedArray()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                runOnUiThread {
                    loading.dismiss()
                }
            }
        }
    }

}