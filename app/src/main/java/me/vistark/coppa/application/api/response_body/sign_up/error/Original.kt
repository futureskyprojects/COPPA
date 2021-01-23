package me.vistark.coppa.application.api.response_body.sign_up.error


import com.google.gson.annotations.SerializedName

data class Original(
    @SerializedName("access_token")
    var accessToken: String = "",
    @SerializedName("expires_in")
    var expiresIn: Int = 0,
    @SerializedName("token_type")
    var tokenType: String = ""
)