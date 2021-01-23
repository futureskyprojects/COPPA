package me.vistark.coppa.application.api.signup.response.success


import com.google.gson.annotations.SerializedName

data class SignupSuccessResponse(
    @SerializedName("message")
    var message: String = "",
    @SerializedName("result")
    var result: Result = Result(),
    @SerializedName("status")
    var status: Int = 0
)