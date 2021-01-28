package me.vistark.coppa.application

import android.Manifest
import me.vistark.coppa.R
import me.vistark.coppa._core.remote_config.RemoteConfig.remoteConfig
import me.vistark.fastdroid.core.models.RequirePermission
import me.vistark.fastdroid.utils.MultipleLanguage.L


object DefaultValue {
    val DefaultPassword = remoteConfig.getString("DefaultPassword")
    val MinImageRequest = remoteConfig.getString("MinImageRequest").toIntOrNull() ?: 0
    val BaseUrl = remoteConfig.getString("BaseUrl")


    val AppPermissions: ArrayList<RequirePermission>
        get() {
            val temp: ArrayList<RequirePermission> = ArrayList()
            temp.add(
                RequirePermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    L("PreciseLocation"), // Đã thêm vào res dịch
                    L("AllowsAnAppToAccessPreciseLocation") // Đã thêm vào res dịch
                )
            )
            temp.add(
                RequirePermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    L("ApproximateLocation"),// Đã thêm vào res dịch
                    L("AllowsAnAppToAccessApproximateLocation")// Đã thêm vào res dịch
                )
            )
            temp.add(
                RequirePermission(
                    Manifest.permission.INTERNET,
                    L("Internet"),// Đã thêm vào res dịch
                    L("AllowsApplicationsToOpenNetworkSockets")// Đã thêm vào res dịch
                )
            )
            temp.add(
                RequirePermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    L("ReadExternalStorage"),// Đã thêm vào res dịch
                    L("AllowsAnApplicationToReadFromExternalStorage")// Đã thêm vào res dịch
                )
            )
            temp.add(
                RequirePermission(
                    Manifest.permission.CAMERA,
                    L("Camera"),// Đã thêm vào res dịch
                    L("RequiredToBeAbleToAccessTheCameraDevice")// Đã thêm vào res dịch
                )
            )
            return temp
        }
}