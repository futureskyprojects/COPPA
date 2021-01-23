package me.vistark.coppa.application.api.response_body.sign_up.error


import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("exception")
    var exception: Any = Any(),
    @SerializedName("headers")
    var headers: Headers = Headers(),
    @SerializedName("original")
    var original: Original = Original()
)