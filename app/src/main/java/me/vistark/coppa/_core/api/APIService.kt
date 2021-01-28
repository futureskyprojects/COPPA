package me.vistark.coppa._core.api

import me.vistark.coppa.application.DefaultValue.BaseUrl
import me.vistark.fastdroid.core.api.FastdroidAPIService

class APIService :
    FastdroidAPIService<IAPIService>(BaseUrl, IAPIService::class.java) {
}