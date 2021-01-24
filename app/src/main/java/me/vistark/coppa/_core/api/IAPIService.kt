package me.vistark.coppa._core.api

import me.vistark.coppa.application.api.captain_profile.response.CaptainProfile
import me.vistark.coppa.application.api.common.ResponseWrapper
import me.vistark.coppa.application.api.signin.request.LoginRequestDTO
import me.vistark.coppa.application.api.signin.response.LoginSuccessResponse
import me.vistark.coppa.application.api.signup.request.RegisterRequestDTO
import me.vistark.coppa.application.api.signup.response.success.SignupSuccessResponse
import me.vistark.fastdroid.core.api.interfaces.IFastdroidAPI
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IAPIService : IFastdroidAPI {
    @POST("register")
    fun postRegister(@Body dto: RegisterRequestDTO): Call<SignupSuccessResponse>

    @POST("login")
    fun postLogin(@Body dto: LoginRequestDTO): Call<ResponseWrapper<LoginSuccessResponse>>

    @POST("captain")
    fun getCaptainProfile(): Call<ResponseWrapper<CaptainProfile>>
}