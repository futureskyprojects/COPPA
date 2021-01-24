package me.vistark.fastdroid.utils.storage

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object FastdroidFileUtils {
    fun Context.saveBitmap(
        bmp: Bitmap,
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
                bmp.compress(
                    Bitmap.CompressFormat.PNG,
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