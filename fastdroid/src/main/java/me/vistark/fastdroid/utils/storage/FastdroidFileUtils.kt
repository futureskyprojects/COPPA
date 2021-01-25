package me.vistark.fastdroid.utils.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object FastdroidFileUtils {
    fun Bitmap.resize(maxSize: Int): Bitmap? {
        val ratio = width.toFloat() / height.toFloat()
        if (width <= maxSize && height <= maxSize)
            return this
        if (width > height) {
            return Bitmap.createScaledBitmap(this, maxSize, (maxSize / ratio).toInt(), false)
        } else {
            return Bitmap.createScaledBitmap(this, (ratio * maxSize).toInt(), maxSize, false)
        }
    }

    fun Context.saveBitmap(
        bmp: Bitmap,
        maxSize: Int = 1024,
        fileName: String = UUID.randomUUID().toString() + ".jpg"
    ): String {
        var dest = externalCacheDir?.path ?: ""
        if (dest.isEmpty()) {
            return ""
        }
        dest += File.separator + fileName
        dest = dest.replace("//", "/")

        val parentPath = File(dest).parent ?: ""
        val f = File(parentPath)
        if (!f.exists()) f.mkdirs()

        try {
            FileOutputStream(dest).use { out ->
                bmp.resize(maxSize)?.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    out
                ) // bmp is your Bitmap instance
            }
            return dest
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }
}