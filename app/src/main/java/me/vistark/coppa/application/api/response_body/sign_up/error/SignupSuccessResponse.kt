package me.vistark.coppa.application.api.response_body.sign_up.error


import com.google.gson.annotations.SerializedName

data class SignupSuccessResponse(
    @SerializedName("message")
    var message: String = "",
    @SerializedName("result")
    var result: Result = Result(),
    @SerializedName("status")
    var status: Int = 0
)