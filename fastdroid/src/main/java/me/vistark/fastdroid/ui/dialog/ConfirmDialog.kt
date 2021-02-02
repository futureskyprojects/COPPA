package me.vistark.fastdroid.ui.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import me.vistark.fastdroid.R
import me.vistark.fastdroid.utils.ViewExtension.onTap

object ConfirmDialog {
    fun Context.deleteConfirm(message: String = "", onConfirm: (Boolean) -> Unit): AlertDialog {
        val v = LayoutInflater.from(this)
            .inflate(R.layout.alert_dialog, null)

        val tvMessage: TextView = v.findViewById(R.id.tvMessage)
        val centerImage: ImageView = v.findViewById(R.id.centerImage)
        val btnCancel: Button = v.findViewById(R.id.btnCancel)
        val btnConfirm: Button = v.findViewById(R.id.btnConfirm)

        Glide.with(this).load(R.raw.remove).into(centerImage)

        tvMessage.text = message

        val mBuilder = AlertDialog
            .Builder(this)
            .setView(v)

        val mAlertDialog = mBuilder.show()
        // Khiến cho dialog wrap content

        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mAlertDialog.setCancelable(false)

        btnCancel.onTap {
            mAlertDialog.dismiss()
            onConfirm.invoke(false)
        }
        btnConfirm.onTap {
            mAlertDialog.dismiss()
            onConfirm.invoke(true)
        }

        return mAlertDialog
    }
}