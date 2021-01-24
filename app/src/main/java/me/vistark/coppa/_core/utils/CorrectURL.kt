package me.vistark.coppa._core.utils

import me.vistark.coppa._core.api.APIService

object CorrectURL {
    fun String.correctPath(): String {
        if (this.startsWith("http"))
            return this
        if (this.startsWith("/"))
            return APIService().baseUrl + ".." + this
        return APIService().baseUrl + "../" + this
    }
}