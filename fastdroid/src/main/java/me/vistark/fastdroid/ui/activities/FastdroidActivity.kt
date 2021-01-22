package me.vistark.fastdroid.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import me.vistark.fastdroid.language.LanguageConfig
import me.vistark.fastdroid.utils.FastdroidContextWrapper
import me.vistark.fastdroid.utils.FastdroidContextWrapper.Companion.forOnCreate
import me.vistark.fastdroid.utils.keyboard.HideKeyboardExtension.Companion.HideKeyboard
import me.vistark.fastdroid.utils.storage.AppStorageManager


abstract class FastdroidActivity(
    val layoutId: Int,
    val isLimit: Boolean = true,
    val isHaveActionBar: Boolean = false
) : AppCompatActivity() {

    var reqOri = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        // Khởi tạo bộ lưu trữ shared preference mặc định
        AppStorageManager.initialize(this)
        // Cấu hình ngôn ngữ
        forOnCreate()
        // Cấu hình ẩn bàn phím khi nhấn bên ngoài
        checkForSetHide(window.decorView.rootView)
        // Full màn hình hoặc không
        if (!isLimit) {
            // Ẩn actionbar hoặc không
            if (!isHaveActionBar)
                supportActionBar?.hide()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            }
        }

        // Ẩn actionbar hoặc không
        if (!isHaveActionBar)
            supportActionBar?.hide()

        // Cấu hình quay
        requestedOrientation = reqOri

        // Đẩy màn hình layout lên khi xuất hiện bàn phím
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            super.attachBaseContext(
                FastdroidContextWrapper.wrap(
                    newBase,
                    LanguageConfig.LanguageCode
                )
            )
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        HideKeyboard()
        return super.onTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun checkForSetHide(view: View) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                HideKeyboard()
                return@setOnTouchListener false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView: View = view.getChildAt(i)
                checkForSetHide(innerView)
            }
        }
    }
}