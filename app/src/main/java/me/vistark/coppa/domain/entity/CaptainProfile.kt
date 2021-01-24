package me.vistark.coppa.domain.entity


import com.google.gson.annotations.SerializedName

data class CaptainProfile(
    @SerializedName("captain")
    var captain: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("culture_name")
    var cultureName: String = "",
    @SerializedName("duration")
    var duration: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("fishing_license")
    var fishingLicense: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("image")
    var image: String = "",
    @SerializedName("password")
    var password: String = "",
    @SerializedName("phone")
    var phone: String = "",
    @SerializedName("shipowner")
    var shipowner: String = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("username")
    var username: String = "",
    @SerializedName("vessel_registration")
    var vesselRegistration: String = ""
)