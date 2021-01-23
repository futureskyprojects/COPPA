package me.vistark.coppa.ui.splash_screen

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_splash.*
import me.vistark.coppa.R
import me.vistark.coppa.ui.auth.AuthActivity
import me.vistark.fastdroid.ui.activities.FastdroidActivity
import me.vistark.fastdroid.utils.GlideUtils.loadImage
import me.vistark.fastdroid.utils.TimerUtils.startAfter

class SplashActivity : FastdroidActivity(R.layout.activity_splash, false) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aloIvLoadingOverlayIcon.loadImage(R.raw.loading_pink)

        startAfter(3000) {
            startAuth()
        }
    }

    fun startAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}