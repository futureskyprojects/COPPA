package me.vistark.coppa.application.api.trans.request


import com.google.gson.annotations.SerializedName

data class CoppaTranslateRequest(
    @SerializedName("culture_name")
    var cultureName: String = ""
)