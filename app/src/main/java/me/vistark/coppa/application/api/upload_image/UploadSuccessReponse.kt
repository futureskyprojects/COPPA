package me.vistark.coppa.application.api.upload_image


import com.google.gson.annotations.SerializedName

data class UploadSuccessReponse(
    @SerializedName("path")
    var path: String = ""
)