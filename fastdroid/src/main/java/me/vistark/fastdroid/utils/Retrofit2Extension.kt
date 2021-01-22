package me.vistark.fastdroid.utils

import android.content.Context
import me.vistark.fastdroid.ui.overlay.LoadingBase.showLoadingBase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Retrofit2Extension {
    suspend fun <T> Call<T>.await(context: Context? = null, loadingMessage: String = ""): T? {
        val loadingBase = context?.showLoadingBase(loadingMessage)
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>?, t: Throwable) {
                    loadingBase?.dismiss()
                    continuation.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>?, response: Response<T>) {
                    loadingBase?.dismiss()
                    if (response.isSuccessful && response.body() != null) {
                        continuation.resume(response.body())
                    } else {
                        continuation.resumeWithException(HttpException(response))
                    }
                }
            })
        }
    }
}