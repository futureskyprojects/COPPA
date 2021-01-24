package me.vistark.coppa.application.api.signin.response


import com.google.gson.annotations.SerializedName
import me.vistark.coppa.application.api._common.Original

data class LoginSuccessResponse(
    @SerializedName("original")
    var original: Original = Original()
)