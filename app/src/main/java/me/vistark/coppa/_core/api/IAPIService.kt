package me.vistark.coppa._core.api

import me.vistark.coppa.domain.entity.CaptainProfile
import me.vistark.coppa.application.api._common.ResponseWrapper
import me.vistark.coppa.application.api.captain_profile.request.UpdateCaptainProfileRequestDTO
import me.vistark.coppa.application.api.signin.request.LoginRequestDTO
import me.vistark.coppa.application.api.signin.response.LoginSuccessResponse
import me.vistark.coppa.application.api.signup.request.RegisterRequestDTO
import me.vistark.coppa.application.api.signup.response.success.SignupSuccessResponse
import me.vistark.coppa.application.api.upload_image.UploadSuccessReponse
import me.vistark.coppa.domain.entity.Species
import me.vistark.coppa.domain.entity.SpeciesCategory
import me.vistark.fastdroid.core.api.interfaces.IFastdroidAPI
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface IAPIService : IFastdroidAPI {
    @POST("register")
    fun postRegister(@Body dto: RegisterRequestDTO): Call<SignupSuccessResponse>

    @POST("login")
    fun postLogin(@Body dto: LoginRequestDTO): Call<ResponseWrapper<LoginSuccessResponse>>

    @POST("captain")
    fun getCaptainProfile(): Call<ResponseWrapper<CaptainProfile>>

    @POST("captain/update")
    fun postUpdateCaptainProfile(@Body dto: UpdateCaptainProfileRequestDTO): Call<ResponseWrapper<Any>>

    @GET("families")
    fun getSpeciesCategories(): Call<ResponseWrapper<ArrayList<SpeciesCategory>>>

    @GET("species")
    fun getSpecieses(): Call<ResponseWrapper<ArrayList<Species>>>

    @Multipart
    @POST("upload/image")
    fun postUploadImage(@Part image: MultipartBody.Part): Call<ResponseWrapper<UploadSuccessReponse>>
}