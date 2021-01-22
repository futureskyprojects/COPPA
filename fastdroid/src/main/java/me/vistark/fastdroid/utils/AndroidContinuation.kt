package me.vistark.fastdroid.utils

import android.os.Handler
import android.os.Looper
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AndroidContinuation<in T>(val continuation: Continuation<T>) :
    Continuation<T> by continuation {
    fun resume(value: T) {
        postOnMainThread { continuation.resume(value) }
    }

    fun resumeWithException(exception: Throwable) {
        postOnMainThread { continuation.resumeWithException(exception) }
    }

    inline fun postOnMainThread(crossinline expr: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) expr()
        else Handler(Looper.getMainLooper()).post { expr() }
    }
}