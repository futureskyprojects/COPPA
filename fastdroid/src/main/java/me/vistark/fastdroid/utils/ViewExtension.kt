package me.vistark.fastdroid.utils

import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import me.vistark.fastdroid.R
import me.vistark.fastdroid.utils.AnimationUtils.doOnEnd
import me.vistark.fastdroid.utils.AnimationUtils.scaleDownCenter
import me.vistark.fastdroid.utils.AnimationUtils.scaleUpCenter
import me.vistark.fastdroid.utils.DateTimeUtils.Companion.format
import me.vistark.fastdroid.utils.DateTimeUtils.Companion.from
import me.vistark.fastdroid.utils.VibrateUtils.vibrate
import me.vistark.fastdroid.utils.keyboard.HideKeyboardExtension.Companion.HideKeyboard
import java.util.*


object ViewExtension {
    fun View.bindPopupMenu(menuResId: Int, onMenuItemClicked: (Int) -> Boolean) {
        onTap {
            //creating a popup menu
            val popup = PopupMenu(context, this)
            //inflating menu from xml resource
            popup.inflate(menuResId)
            //adding click listener
            popup.setOnMenuItemClickListener { item ->
                return@setOnMenuItemClickListener onMenuItemClicked(item.itemId)
            }
            //displaying the popup
            popup.show()
        }
    }

    fun View.hide() {
        if (View.VISIBLE == View.VISIBLE)
            scaleDownCenter(150)
//        this.visibility = View.GONE
    }

    fun View.show() {
        if (View.VISIBLE == View.GONE)
            scaleUpCenter(150)
//        this.visibility = View.VISIBLE
    }

    fun View.binDateTimePicker(
        date: Date = Calendar.getInstance().time,
        onResult: ((Date) -> Unit)
    ) {
        onTap {
            HideKeyboard()
            showDatePicker(date) { pickedDate ->
                val calendar = Calendar.getInstance()
                calendar.time = pickedDate
                showTimePicker(pickedDate) { hourOfDay, minutes ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minutes)
                    calendar.set(Calendar.SECOND, 0)

                    onResult.invoke(calendar.time)

                    if (this is TextView) {
                        this.text = calendar.time.format("yyyy-MM-dd HH:mm:ss")
                    }
                }
            }
        }
    }

    fun View.showDatePicker(date: Date = Calendar.getInstance().time, onResult: ((Date) -> Unit)) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val x = DatePickerDialog(
            this.context,
            { v, year, month, dayOfMonth ->
                val dateRes = Date().from(year, month, dayOfMonth)
                if (this is TextView) {
                    this.text = dateRes.format()
                }
                onResult.invoke(dateRes)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        x.datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { v, year, month, dayOfMonth ->
            val dateRes = Date().from(year, month, dayOfMonth)
            if (this is TextView) {
                this.text = dateRes.format()
            }
            onResult.invoke(dateRes)
            x.cancel()
        }
        x.show()
    }

    fun View.bindDatePicker(date: Date = Calendar.getInstance().time, onResult: ((Date) -> Unit)) {
        this.onTap {
            HideKeyboard()
            showDatePicker(date, onResult)
        }
    }

    fun View.showTimePicker(
        date: Date = Calendar.getInstance().time,
        onResult: ((Int, Int) -> Unit)
    ) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val x = TimePickerDialog(
            context,
            { view, hourOfDay, minute ->
                if (this is TextView) {
                    this.text = String.format("%2d:%2d", hourOfDay, minute)
                }
                onResult.invoke(hourOfDay, minute)
            }, calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), true
        )
        x.updateTime(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )
        x.show()
    }

    fun View.bindTimePicker(
        date: Date = Calendar.getInstance().time,
        onResult: ((Int, Int) -> Unit)
    ) {
        this.onTap {
            HideKeyboard()
            showTimePicker(date, onResult)
        }
    }

    fun ScrollView.moveToTop(duration: Long = 100L) {
        val objectAnimator =
            ObjectAnimator.ofInt(this, "scrollY", this.scrollY, 0).setDuration(duration)
        objectAnimator.start()
//        this.fullScroll(ScrollView.FOCUS_UP)
    }

    fun EditText.onChanged(changed: () -> Unit) {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                changed.invoke()
            }
        })
    }

    fun EditText.onTextChanged(changed: (String) -> Unit) {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                changed.invoke(p0?.toString()?.trim() ?: "")
            }
        })
    }

    fun View.onTap(f: () -> Unit) {
        val anim = AnimationUtils.loadAnimation(this.context, R.anim.scale_bounce)
        this.setOnClickListener {
            vibrate(30)
            this.isEnabled = false
            this.startAnimation(anim)
            this.postDelayed(f, anim.duration + 10)
        }
        anim.doOnEnd {
            this.postDelayed({
                this.isEnabled = true
            }, 100)
        }
    }

    fun View.slideDownShow(onFinish: (() -> Unit)? = null) {
        val anim = AnimationUtils.loadAnimation(this.context, R.anim.slide_down_show)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                this@slideDownShow.visibility = View.VISIBLE
                onFinish?.invoke()
            }

            override fun onAnimationStart(p0: Animation?) {
                this@slideDownShow.visibility = View.INVISIBLE
            }
        })
        this.startAnimation(anim)
    }

    fun View.slideUpHide(onFinish: (() -> Unit)? = null) {
        val anim = AnimationUtils.loadAnimation(this.context, R.anim.slide_up_hide)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                this@slideUpHide.visibility = View.GONE
                onFinish?.invoke()
            }

            override fun onAnimationStart(p0: Animation?) {
                this@slideUpHide.visibility = View.VISIBLE
            }
        })
        this.startAnimation(anim)
    }

    fun View.slideUp(onFinish: (() -> Unit)? = null) {
        val anim = AnimationUtils.loadAnimation(this.context, R.anim.slide_up)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                this@slideUp.visibility = View.VISIBLE
                onFinish?.invoke()
            }

            override fun onAnimationStart(p0: Animation?) {
                this@slideUp.visibility = View.INVISIBLE
            }
        })
        this.startAnimation(anim)
    }

    fun View.slideDown(onFinish: (() -> Unit)? = null) {
        val anim = AnimationUtils.loadAnimation(this.context, R.anim.slide_down)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                this@slideDown.visibility = View.INVISIBLE
                onFinish?.invoke()
            }

            override fun onAnimationStart(p0: Animation?) {
                this@slideDown.visibility = View.VISIBLE
            }
        })
        this.startAnimation(anim)
    }

    fun CardView.setMargin(
        left: Int = marginLeft,
        top: Int = marginTop,
        right: Int = marginRight,
        bottom: Int = marginBottom
    ) {
        (layoutParams as ViewGroup.MarginLayoutParams).setMargins(left, top, right, bottom)
        requestLayout()
    }
}