package me.vistark.fastdroid.utils

import android.animation.ObjectAnimator
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ScrollView
import me.vistark.fastdroid.R
import java.util.*


object ViewExtension {
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

    fun View.onTap(f: () -> Unit) {
        val anim = AnimationUtils.loadAnimation(this.context, R.anim.scale_bounce)
        this.setOnClickListener {
            this.startAnimation(anim)
            this.postDelayed(f, anim.duration + 10)
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
}