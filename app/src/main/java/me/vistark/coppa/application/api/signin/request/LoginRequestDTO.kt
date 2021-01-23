package me.vistark.coppa.application.api.signin.request


import com.google.gson.annotations.SerializedName

data class LoginRequestDTO(
    @SerializedName("password")
    var password: String = "",
    @SerializedName("username")
    var username: String = ""
)