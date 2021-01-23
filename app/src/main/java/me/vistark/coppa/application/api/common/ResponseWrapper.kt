package me.vistark.coppa.application.api.common


import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import retrofit2.HttpException
import java.lang.Exception

data class ResponseWrapper<T>(
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("data")
    var result: T? = null
)