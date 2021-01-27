package me.vistark.coppa.application.api.signup.response.success


import com.google.gson.annotations.SerializedName
import me.vistark.coppa.application.api.signin.response.LoginSuccessResponse

data class SignupSuccessResponse(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("vessel_registration")
    var vesselRegistration: String = String(),
    @SerializedName("captain")
    var captain: String = String(),
    @SerializedName("token")
    var token: LoginSuccessResponse = LoginSuccessResponse()
)