package me.vistark.coppa.application.api.captain_profile.request


import com.google.gson.annotations.SerializedName

data class UpdateCaptainProfileRequestDTO(
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
    var shipowner: String = ""
)