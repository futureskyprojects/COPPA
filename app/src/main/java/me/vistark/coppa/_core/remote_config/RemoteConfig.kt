package me.vistark.coppa._core.remote_config

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import me.vistark.coppa.R
import me.vistark.coppa.application.DefaultValue.BaseUrl
import me.vistark.coppa.application.DefaultValue.DefaultPassword
import me.vistark.coppa.application.DefaultValue.MinImageRequest

object RemoteConfig {
    val remoteConfig = Firebase.remoteConfig
    val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 1
    }

    fun AppCompatActivity.initRemoteConfig(onCompleted: (() -> Unit)? = null) {
        remoteConfig.setConfigSettingsAsync(configSettings)
//        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val updated = task.result
//                    Log.w("[REMOTE_CONFIG]", "Config params updated: $updated")
//                } else {
//                    Log.e("[REMOTE_CONFIG]", "faild...........")
//                }
//
//                println(">>>>>>>>>>> [DefaultPassword]: $DefaultPassword")
//                println(">>>>>>>>>>> [MinImageRequest]: $MinImageRequest")
//                println(">>>>>>>>>>> [BaseUrl]: $BaseUrl")

                onCompleted?.invoke()
            }
    }
}