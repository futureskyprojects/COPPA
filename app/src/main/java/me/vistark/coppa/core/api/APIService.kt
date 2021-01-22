package me.vistark.coppa.core.api

import me.vistark.fastdroid.core.api.FastdroidAPIService

class APIService :
    FastdroidAPIService<IAPIService>("http://vistark.me", IAPIService::class.java) {
}