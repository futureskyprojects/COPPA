package me.vistark.coppa.application.api.response_body.sign_up.error


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("captain")
    var captain: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("token")
    var token: Token = Token(),
    @SerializedName("vessel_registration")
    var vesselRegistration: String = ""
)