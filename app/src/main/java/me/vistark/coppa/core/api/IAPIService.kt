package me.vistark.coppa.core.api

import me.vistark.coppa.application.api.request_body.sign_up.RegisterRequestDTO
import me.vistark.coppa.application.api.response_body.sign_up.ErrorResponse
import me.vistark.coppa.application.api.response_body.sign_up.error.SignupSuccessResponse
import me.vistark.fastdroid.core.api.interfaces.IFastdroidAPI
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IAPIService : IFastdroidAPI {
    @POST("register")
    fun postRegister(@Body dto: RegisterRequestDTO): Call<SignupSuccessResponse>
}