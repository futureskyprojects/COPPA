package me.vistark.fastdroid.utils

import android.util.Log
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Retrofit2Extension {
    companion object {
        suspend fun <T> Call<T>.await(): T? {
            return suspendCoroutine { continuation ->
                enqueue(object : Callback<T> {
                    override fun onFailure(call: Call<T>?, t: Throwable) {
                        Log.e(
                            "[${call?.request()?.method()}]",
                            "[${call?.request()?.url()}]:${
                                Gson().toJson(
                                    call?.request()?.body()
                                )
                            }" +
                                    " >>>> ${t.message}"
                        )
                        continuation.resumeWithException(t)
                    }

                    override fun onResponse(call: Call<T>?, response: Response<T>) {
                        Log.w(
                            "[${call?.request()?.method()}]",
                            "[${call?.request()?.url()}]:${
                                Gson().toJson(
                                    call?.request()?.body()
                                )
                            }" + " >>>> [${response.message()}]\r\n${Gson().toJson(response.body())}"
                        )
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
}