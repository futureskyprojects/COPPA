package me.vistark.coppa.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.google.gson.Gson
import me.vistark.coppa.R
import me.vistark.fastdroid.broadcasts.FastdroidBroadcastReceiver.Companion.sendSignal
import me.vistark.fastdroid.core.models.FastdroidCoordinate
import me.vistark.fastdroid.services.FastdroidService
import me.vistark.fastdroid.utils.MultipleLanguage.L
import me.vistark.fastdroid.utils.PermissionUtils.isPermissionGranted
import java.util.*


class BackgroundService : FastdroidService(
    "COPPA",
    R.mipmap.ic_launcher_round,
    L("CoppaIsRunningInBackground")
), LocationListener {

    // Thời gian làm mới
    val LOCATION_REFRESH_TIME = 2000L

    // Khoảng cách tương đối có thể chấp nhận
    val LOCATION_REFRESH_DISTANCE = 1F

    var mLocationManager: LocationManager? = null

    companion object {
        val REALTIME_COORDINATES = "REALTIME_COORDINATES"
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
    }

    override fun onLocationChanged(location: Location) {
        val coordinate = FastdroidCoordinate(location.latitude, location.longitude)
        val coordinateInString = Gson().toJson(coordinate)
        sendSignal(REALTIME_COORDINATES, coordinateInString)
    }
}