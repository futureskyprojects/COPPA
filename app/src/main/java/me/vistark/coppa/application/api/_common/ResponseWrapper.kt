package me.vistark.coppa.application.api._common


import com.google.gson.annotations.SerializedName

data class ResponseWrapper<T>(
    @SerializedName("message")
    var message: ArrayList<String> = ArrayList(),
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("result")
    var result: T? = null
)