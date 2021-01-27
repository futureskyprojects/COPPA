package me.vistark.fastdroid.utils.storage

import android.content.Context
import android.graphics.Bitmap
import java.io.*
import java.util.*

object FastdroidFileUtils {
    var CONTENT_ROOT = ""
    var TEMP_DIR = ""

    fun Context.initFileUtils() {
        CONTENT_ROOT = externalCacheDir?.path ?: ""
        TEMP_DIR = (CONTENT_ROOT + File.separator + ".vistark_temp").replace("//", "/")
    }

    fun String.correctPath(vararg name: String = emptyArray()): String {
        var temp = this
        name.forEach {
            temp += File.separator + it
        }
        temp = temp.replace("//", "/")
        return temp
    }


    fun String.copyTo(dst: String): String {
        val f1 = File(this)
        val f2 = File(dst)

        try {
            if (f1.exists())
                return ""
            return f1.copyTo(f2, true).absolutePath
        } catch (e: Exception) {
            return ""
        }
    }

    fun String.deleteOnExists(): Boolean {
        val f = File(this)
        if (!f.exists())
            return true

        return if (f.isDirectory) {
            f.deleteRecursively()
        } else {
            f.delete()
        }
    }

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

    fun Context.createTempFile(fileName: String): File? {
        var dest = externalCacheDir?.path ?: ""
        if (dest.isEmpty()) {
            return null
        }
        dest += File.separator + ".vistark_temp" + File.separator + fileName
        dest = dest.replace("//", "/")

        val parentPath = File(dest).parent ?: ""
        val f = File(parentPath)
        if (!f.exists()) f.mkdirs()
        val res = File(dest)
        if (res.exists())
            res.delete()
        res.createNewFile()
        res.deleteOnExit()
        return res
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