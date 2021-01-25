package me.vistark.coppa.application

import android.Manifest
import me.vistark.fastdroid.core.models.RequirePermission
import me.vistark.fastdroid.utils.MultipleLanguage.L

object DefaultValue {
    const val DefaultPassword = "123456"
    const val MinImageRequest = 2
    val AppPermissions: ArrayList<RequirePermission>
        get() {
            val temp: ArrayList<RequirePermission> = ArrayList()
            temp.add(
                RequirePermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    L("PreciseLocation"),
                    L("AllowsAnAppToAccessPreciseLocation")
                )
            )
            temp.add(
                RequirePermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    L("ApproximateLocation"),
                    L("AllowsAnAppToAccessApproximateLocation")
                )
            )
            temp.add(
                RequirePermission(
                    Manifest.permission.INTERNET,
                    L("Internet"),
                    L("AllowsApplicationsToOpenNetworkSockets")
                )
            )
            temp.add(
                RequirePermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    L("ReadExternalStorage"),
                    L("AllowsAnApplicationToReadFromExternalStorage")
                )
            )
            temp.add(
                RequirePermission(
                    Manifest.permission.CAMERA,
                    L("Camera"),
                    L("RequiredToBeAbleToAccessTheCameraDevice")
                )
            )
            return temp
        }
}