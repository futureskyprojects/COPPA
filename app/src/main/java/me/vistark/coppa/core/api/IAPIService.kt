package me.vistark.coppa.core.api

import me.vistark.coppa.application.api.signup.request.RegisterRequestDTO
import me.vistark.coppa.application.api.signup.response.success.SignupSuccessResponse
import me.vistark.fastdroid.core.api.interfaces.IFastdroidAPI
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IAPIService : IFastdroidAPI {
    @POST("register")
    fun postRegister(@Body dto: RegisterRequestDTO): Call<SignupSuccessResponse>
}