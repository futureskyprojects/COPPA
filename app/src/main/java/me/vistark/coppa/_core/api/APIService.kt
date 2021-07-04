package me.vistark.coppa._core.api

import android.util.Log
import me.vistark.coppa.application.DefaultValue.BaseUrl
import me.vistark.fastdroid.core.api.FastdroidAPIService

class APIService :
    FastdroidAPIService<IAPIService>(BaseUrl, IAPIService::class.java) {
        init {
            Log.w("BASE_URL", baseUrl)
        }
}