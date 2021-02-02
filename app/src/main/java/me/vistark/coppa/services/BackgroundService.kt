package me.vistark.coppa.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.vistark.coppa.R
import me.vistark.coppa._core.api.APIService
import me.vistark.coppa.application.RuntimeStorage
import me.vistark.coppa.application.api.trip_sync.request.TripSyncDTO
import me.vistark.coppa.domain.entity.TripSync
import me.vistark.coppa.domain.entity.TripSync.Companion.removeTripSync
import me.vistark.coppa.domain.entity.TripSync.Companion.updateTripSyncHauls
import me.vistark.fastdroid.broadcasts.FastdroidBroadcastReceiver.Companion.sendSignal
import me.vistark.fastdroid.core.models.FastdroidCoordinate
import me.vistark.fastdroid.services.FastdroidService
import me.vistark.fastdroid.utils.InternetUtils.isInternetAvailable
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.ObjectUtils.clone
import me.vistark.fastdroid.utils.PermissionUtils.isPermissionGranted
import me.vistark.fastdroid.utils.storage.FastdroidFileUtils.deleteOnExists
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import me.vistark.fastdroid.utils.Retrofit2Extension.Companion.await
import java.io.File
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList


class BackgroundService : FastdroidService(
    L("app_name"), // Đã thêm vào res dịch
    R.mipmap.ic_launcher_round,
    L("AppIsRunningInBackground") // Đã dịch
), LocationListener {

    var isSyncing = false

    // Thời gian làm mới
    val LOCATION_REFRESH_TIME = 2000L

    // Khoảng cách tương đối có thể chấp nhận
    val LOCATION_REFRESH_DISTANCE = 1F

    var mLocationManager: LocationManager? = null

    var lastedDetectCoordinate: Long = 0

    var timeForWaitGPSSignal = 10000

    var timer: Timer? = null

    companion object {
        val REALTIME_COORDINATES = "REALTIME_COORDINATES"
        val SYNC_DONE = "SYNC_DONE"
    }

    @SuppressLint("MissingPermission")
    fun syncLocation() {
        if (mLocationManager != null) {
            mLocationManager?.removeUpdates(this)
        }
        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        // Lấy vị trí được hệ thống thu thập lần cuối cùng
        val criteria = Criteria()
        val provider = mLocationManager?.getBestProvider(criteria, false)
        val location: Location? = provider?.let { mLocationManager?.getLastKnownLocation(it) }
        if (location != null) {
            onLocationChanged(location)
            Log.e(
                "[SERVICE]location",
                "Latest know: [${location.latitude}, ${location.longitude}]"
            )
        } else {
            Log.e("[SERVICE]location", "Not available")
        }
        mLocationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
            LOCATION_REFRESH_DISTANCE, this
        )
    }

    @SuppressLint("MissingPermission")
    override fun tasks() {
        if (isPermissionGranted(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            syncLocation()
        }

        internetTimerChecker()

        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (System.currentTimeMillis() - lastedDetectCoordinate > timeForWaitGPSSignal) {
                    Handler(Looper.getMainLooper()).post {
                        syncLocation()
                    }
                }
            }

        }, 1000, 1000)
    }

    override fun onLocationChanged(location: Location) {
        lastedDetectCoordinate = System.currentTimeMillis()
        val coordinate = FastdroidCoordinate(location.latitude, location.longitude)
        val coordinateInString = Gson().toJson(coordinate)
        sendSignal(REALTIME_COORDINATES, coordinateInString)
    }

    private fun internetTimerChecker() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (isInternetAvailable()) {
                    timer?.cancel()
                    timer?.purge()
                    timer = null
                    sync()
                } else {
                    updateDefault()
                }
            }
        }, 1000, 3000)
    }

    fun sync() {
        updateDefault()
        isSyncing = true
        val tripSync = RuntimeStorage.TripSyncs.firstOrNull()
        if (tripSync == null || RuntimeStorage.TripSyncs.isEmpty()) {
            internetTimerChecker()
            return
        }

        updateLoading(
            String.format(
                L(getString(R.string.Sync_Trip)),
                RuntimeStorage.TripSyncs.size
            )
        )

        GlobalScope.launch {
            val apis = APIService().APIs
            // Upload từng tệp ảnh lên server
            for (i in 0 until tripSync.hauls.size) {
                // Tách ra danh sách các file ảnh
                val images = tripSync.hauls[i].images.split(",").filter { it.isNotEmpty() && it.isNotBlank() }
                // Upload từng file ảnh lên
                images.forEach {
                    if (it.contains(TripSync.SUB_CURRENT_TRIP_FOLDER)) {
                        // Nếu path là local thì tiến hành upload, không thì bỏ qua
                        Log.w("UPLOAD_IMAGE", "[$it]")
                        // Tạo file body
                        val file = File(it)
                        val requestFile: RequestBody =
                            RequestBody.create(MediaType.parse("multipart/form-data"), file)
                        val body =
                            MultipartBody.Part.createFormData("image", file.name, requestFile)

                        // Tải lên máy chủ
                        try {
                            val res = apis.postUploadImage(body).await()
                            if (res!!.status == 200 && res.result?.path?.isNotEmpty() == true) {
                                // Cập nhật vào ảnh của chuyến
                                val _imgs =
                                    tripSync.hauls[i].images.split(",").filter { it.isNotEmpty() && it.isNotBlank() }.filter { p -> p != it }
                                tripSync.hauls[i].images =
                                    _imgs.plusElement(res.result!!.path).joinToString(",")

                                updateTripSyncHauls(tripSync.hauls[i])
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            if (tripSync.isSyncedAllImages()) {
                // Nếu tất cả các ảnh trong chuyến đã được đồng bộ thì tiến hành gửi thông tin chuyến lên
                // Cập nhật thời điểm gửi
                tripSync.updateSubmitTime()

                // Gửi lên server
                try {
                    val res = apis.postSync(TripSyncDTO(tripSync)).await()
                    if (res!!.status == 200) {
                        // Xóa hết cá tệp tin trong này
                        tripSync.hauls.forEach { h ->
                            h.images.split(",").filter { it.isNotEmpty() && it.isNotBlank() }.forEach { img ->
                                img.deleteOnExists()
                            }
                        }
                        // Xóa chuyến
                        removeTripSync(tripSync)
                    }
                    internetTimerChecker()
                    isSyncing = false
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (!isInternetAvailable()) {
                        internetTimerChecker()
                        isSyncing = false
                    }
                }
            } else {
                // Không thì tiến hành cho chạy timer để thực hiện lại
                internetTimerChecker()
                isSyncing = false
            }
        }

    }
}