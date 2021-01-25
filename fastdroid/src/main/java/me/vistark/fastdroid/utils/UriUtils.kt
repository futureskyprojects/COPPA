package me.vistark.fastdroid.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.bumptech.glide.Glide
import me.vistark.fastdroid.R
import me.vistark.fastdroid.utils.storage.FastdroidFileUtils.saveBitmap
import java.util.*

object UriUtils {
    fun Uri.bitmap(context: Context): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        context.contentResolver,
                        this
                    )
                )
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, this)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun Uri.saveImage(
        context: Context,
        maxSize: Int = 1024,
        filename: String = UUID.randomUUID().toString() + ".jpg"
    ): String {
        return context.saveBitmap(
            bitmap(context) ?: return "", maxSize, filename
        )
    }
}