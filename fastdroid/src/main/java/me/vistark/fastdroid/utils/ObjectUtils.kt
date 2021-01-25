package me.vistark.fastdroid.utils

import com.google.gson.Gson

object ObjectUtils {
    inline fun <reified T> T.clone(): T {
        val str = Gson().toJson(this)
        return Gson().fromJson(str, T::class.java)
    }
}