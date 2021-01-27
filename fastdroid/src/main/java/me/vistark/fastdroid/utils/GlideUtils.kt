package me.vistark.fastdroid.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import me.vistark.fastdroid.R
import me.vistark.fastdroid.utils.InternetUtils.isInternetAvailable
import me.vistark.fastdroid.utils.SecurityHashUtils.md5
import me.vistark.fastdroid.utils.storage.AppStorageManager
import me.vistark.fastdroid.utils.storage.FastdroidFileUtils.saveBitmap
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.concurrent.ExecutionException


object GlideUtils {

    fun ImageView.load(url: String) {
        Glide.with(context).load(url)
            .placeholder(R.drawable.fastdroid_holder)
            .into(this)
    }

    fun ImageView.load(resId: Int) {
        Glide.with(context).load(resId)
            .placeholder(R.drawable.fastdroid_holder)
            .into(this)
    }

    fun ImageView.path(filePath: String, holderResId: Int = R.drawable.fastdroid_holder) {
        val f = File(filePath)
        if (f.exists()) {
            Log.w("ImageView[Path]", "[$filePath] is exists!")
            val myBitmap = BitmapFactory.decodeFile(f.absolutePath)
            setImageBitmap(myBitmap)
        } else {
            Log.e("ImageView[Path]", "[$filePath] is not exists!")
            setImageResource(holderResId)
        }
    }

    fun ImageView.load(url: String, isCache: Boolean) {
        if (!isCache)
            load(url)
        else {
            val genKey = url.md5() + "." + url.length
            val snapshotPath: String = AppStorageManager.get(genKey) ?: ""
            var isSaved: Boolean = false
            path(snapshotPath)
            if (isInternetAvailable()) {
                Thread {
                    try {
                        val path = context.saveImage(
                            url,
                            filename = "/cache/${UUID.randomUUID()}.jpg"
                        )
                        if (path.isNotEmpty()) {
                            try {
                                File(snapshotPath).delete()
                            } catch (z: Exception) {
                                z.printStackTrace()
                            }
                            isSaved = true
                        }
                        AppStorageManager.update(genKey, path)

                        post {
                            path(path)
                        }
                    } catch (ex: ExecutionException) {

                    } catch (e: Exception) {
                        if (snapshotPath.isEmpty() && !isSaved)
                            post { setImageResource(R.drawable.fastdroid_holder) }
                        e.printStackTrace()
                    }
                }.start()
            }
        }
    }

    fun Context.saveImage(
        url: String,
        maxSize: Int = 1024,
        filename: String = UUID.randomUUID().toString() + ".jpg"
    ): String {
        Log.w("[GLIDE]", "Save from [$url]")
        return saveBitmap(
            Glide.with(this)
                .asBitmap()
                .load(url)
                .submit()
                .get(), maxSize, filename
        )
    }
}