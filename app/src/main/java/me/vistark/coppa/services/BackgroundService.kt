package me.vistark.coppa.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import me.vistark.fastdroid.broadcasts.FastdroidBroadcastReceiver.Companion.sendSignal
import me.vistark.fastdroid.core.models.FastdroidCoordinate
import me.vistark.fastdroid.services.FastdroidService
import me.vistark.fastdroid.utils.InternetUtils.isInternetAvailable
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.ObjectUtils.clone
import me.vistark.fastdroid.utils.PermissionUtils.isPermissionGranted
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.await
import java.io.File
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList


class BackgroundService : FastdroidService(
    "COPPA",
    R.mipmap.ic_launcher_round,
    L("CoppaIsRunningInBackground")
), LocationListener {

    var isSyncing = false

    // Thời gian làm mới
    val LOCATION_REFRESH_TIME = 2000L

    // Khoảng cách tương đối có thể chấp nhận
    val LOCATION_REFRESH_DISTANCE = 1F

    var mLocationManager: LocationManager? = null

    var timer: Timer? = null

    companion object {
        val REALTIME_COORDINATES = "REALTIME_COORDINATES"
        val SYNC_DONE = "SYNC_DONE"
    }

    @SuppressLint("MissingPermission")
    override fun tasks() {
        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (isPermissionGranted(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Lấy vị trí được hệ thống thu thập lần cuối cùng
            val criteria = Criteria()
            val provider = mLocationManager?.getBestProvider(criteria, false)
            val location: Location? = provider?.let { mLocationManager?.getLastKnownLocation(it) }
            if (location != null) {
                onLocationChanged(location)
            } else {
                println("Location not avilable")
            }
            mLocationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, this
            )
        }

        internetTimerChecker()
    }

    override fun onLocationChanged(location: Location) {
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
        val tripSyncClone = tripSync.clone()

        updateLoading(
            String.format(
                L(getString(R.string.Sync_Trip)),
                RuntimeStorage.TripSyncs.size
            )
        )

        val apis = APIService().APIs
        // Upload từng tệp ảnh lên server
        for (i in 0 until tripSyncClone.hauls.size) {
            // Tách ra danh sách các file ảnh
            val images = tripSyncClone.hauls[i].images.split(",")
            // Tạo bộ nhớ chứa
            val uploadedImages = ArrayList<String>()
            // Upload từng file ảnh lên
            images.forEach {
                // Tạo file body
                val file = File(it)
                val requestFile: RequestBody =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val body =
                    MultipartBody.Part.createFormData("image", file.name, requestFile)

                GlobalScope.launch {
                    // Tải lên máy chủ
                    try {
                        val res = apis.postUploadImage(body).await()
                        if (res.status == 200 && res.result?.path?.isNotEmpty() == true) {
                            uploadedImages.add(res.result!!.path)
                            Log.w("UPLOADED_IMAGES", res.result!!.path)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // Cập nhật danh sách ảnh vừa tải vào lại cho mẻ loài
            tripSyncClone.hauls[i].images = uploadedImages.joinToString(",")
        }
        // Cập nhật thời điểm gửi
        tripSyncClone.updateSubmitTime()

        GlobalScope.launch {
            // Gửi lên server
            try {
                val res = apis.postSync(TripSyncDTO(tripSync)).await()
                if (res.status == 200) {
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
        }

    }
}