package me.vistark.coppa.ui.splash_screen

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_splash.*
import me.vistark.coppa.R
import me.vistark.coppa.ui.auth.AuthActivity
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.utils.GlideUtils.loadImage
import me.vistark.fastdroid.utils.TimerUtils.startAfter

class SplashActivity : FastdroidActivity(
    R.layout.activity_splash,
    isLimit = false,
    windowBackground = R.drawable.bg_window_45
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aloIvLoadingOverlayIcon.loadImage(R.raw.loading_pink)

        startAfter(3000) {
            startAuth()
        }
    }

    fun startAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        startSingleActivity(intent)
    }
}