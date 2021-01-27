package me.vistark.coppa.domain.entity


import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("culture_name")
    var cultureName: String = "",
    @SerializedName("display_name")
    var displayName: String = "",
    @SerializedName("flag_icon")
    var flagIcon: String = ""
)