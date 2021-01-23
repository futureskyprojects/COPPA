package me.vistark.coppa.application.api.request_body.sign_up


import com.google.gson.annotations.SerializedName

data class RegisterRequestDTO(
    @SerializedName("captain")
    var captain: String = "",
    @SerializedName("culture_name")
    var cultureName: String = "",
    @SerializedName("duration")
    var duration: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("fishing_license")
    var fishingLicense: String = "",
    @SerializedName("image")
    var image: String = "",
    @SerializedName("password")
    var password: String = "",
    @SerializedName("phone")
    var phone: String = "",
    @SerializedName("shipowner")
    var shipowner: String = "",
    @SerializedName("username")
    var username: String = "",
    @SerializedName("vessel_registration")
    var vesselRegistration: String = ""
)