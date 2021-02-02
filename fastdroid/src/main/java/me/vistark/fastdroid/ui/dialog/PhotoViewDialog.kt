package me.vistark.fastdroid.ui.dialog

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.dialog_photo_view.view.*
import me.vistark.fastdroid.R
import me.vistark.fastdroid.utils.GlideUtils.load
import me.vistark.fastdroid.utils.ViewExtension.onTap
import java.io.File

object PhotoViewDialog {
    private fun View.bindZoomView(onBind: (PhotoView) -> Unit): AlertDialog {
        val v = LayoutInflater.from(context)
            .inflate(R.layout.dialog_photo_view, null)

        val photoView: PhotoView = v.findViewById(R.id.photoView)
        val closePhotoView: ImageView = v.findViewById(R.id.closePhotoView)
        onBind.invoke(photoView)

        val mBuilder = AlertDialog
            .Builder(context)
            .setView(v)

        val mAlertDialog = mBuilder.show()
        // Khiến cho dialog wrap content

        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mAlertDialog.setCancelable(true)

        closePhotoView.onTap {
            mAlertDialog.dismiss()
        }

        return mAlertDialog
    }

    fun View.bindZoomView(data: Uri?): AlertDialog {
        return bindZoomView {
            it.setImageURI(data)
        }
    }

    fun View.bindZoomView(data: Drawable): AlertDialog {
        return bindZoomView {
            it.setImageDrawable(data)
        }
    }

    fun View.bindZoomView(data: Bitmap): AlertDialog {
        return bindZoomView {
            it.setImageBitmap(data)
        }
    }

    fun View.bindZoomView(data: File): AlertDialog {
        return bindZoomView {
            Glide.with(this).load(data).into(it)
        }
    }

    fun View.bindZoomView(data: ByteArray): AlertDialog {
        return bindZoomView {
            Glide.with(this).load(data).into(it)
        }
    }

    fun View.bindZoomView(data: Int): AlertDialog {
        return bindZoomView {
            it.setImageResource(data)
        }
    }

    fun View.bindZoomView(data: String): AlertDialog {
        return bindZoomView {
            it.load(data)
        }
    }
}