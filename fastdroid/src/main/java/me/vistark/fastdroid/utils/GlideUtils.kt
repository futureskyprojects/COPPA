package me.vistark.fastdroid.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import me.vistark.fastdroid.R
import me.vistark.fastdroid.ui.overlay.LoadingBase.showLoadingBase

object GlideUtils {
    fun ImageView.loadImage(url: String) {
        Glide.with(context).load(url).into(this)
    }

    fun ImageView.loadImage(resId: Int) {
        Glide.with(context).load(resId).into(this)
    }
}