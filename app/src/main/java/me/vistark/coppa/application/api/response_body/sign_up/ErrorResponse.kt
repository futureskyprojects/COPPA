package me.vistark.coppa.application.api.response_body.sign_up


import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import retrofit2.HttpException
import java.lang.Exception

data class ErrorResponse(
    @SerializedName("errors")
    var errors: Any? = Any(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
) {
    constructor(he: HttpException) : this() {
        try {
            val errorBody =
                Gson().fromJson(
                    he.response()?.errorBody().toString(),
                    ErrorResponse::class.java
                )
            this.errors = errorBody.errors
            this.message = errorBody.message
            this.status = errorBody.status
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun extractMessage(): String {
        try {
            if (errors != null) {
                val temp = errors.toString()
                val rawArrJson =
                    "\\[(.*?)\\]".toRegex().find(temp)?.groupValues?.firstOrNull() ?: ""
                println(">>>> [$rawArrJson]")
                val res = JsonParser.parseString(rawArrJson).asJsonArray
                return res.firstOrNull()?.asString ?: ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "UnexpectedError"
    }
}