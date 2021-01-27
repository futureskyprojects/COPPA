package me.vistark.coppa._core.api

import me.vistark.coppa.application.api._common.ResponseWrapper
import me.vistark.coppa.application.api.captain_profile.request.UpdateCaptainProfileRequestDTO
import me.vistark.coppa.application.api.signin.request.LoginRequestDTO
import me.vistark.coppa.application.api.signin.response.LoginSuccessResponse
import me.vistark.coppa.application.api.signup.request.RegisterRequestDTO
import me.vistark.coppa.application.api.signup.response.success.SignupSuccessResponse
import me.vistark.coppa.application.api.trans.request.CoppaTranslateRequest
import me.vistark.coppa.application.api.trip_sync.request.TripSyncDTO
import me.vistark.coppa.application.api.upload_image.UploadSuccessReponse
import me.vistark.coppa.domain.entity.*
import me.vistark.coppa.domain.entity.languages.CoppaTrans
import me.vistark.fastdroid.core.api.interfaces.IFastdroidAPI
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface IAPIService : IFastdroidAPI {
    @POST("register")
    fun postRegister(@Body dto: RegisterRequestDTO): Call<ResponseWrapper<SignupSuccessResponse>>

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

    @GET("ports")
    fun getSeaPorts(): Call<ResponseWrapper<ArrayList<SeaPort>>>

    @GET("old-trip")
    fun getOldTrip(): Call<ResponseWrapper<ArrayList<OldTrip>>>

    @GET("countries")
    fun getCountries(): Call<ResponseWrapper<ArrayList<Country>>>

    @POST("langues")
    fun getDefaultLanguages(): Call<ResponseWrapper<CoppaTrans>>

    @POST("langues")
    fun getLanguages(@Body dto: CoppaTranslateRequest): Call<ResponseWrapper<CoppaTrans>>

    @GET("histories")
    fun getTripHistories(): Call<ResponseWrapper<ArrayList<TripLog>>>

    @Multipart
    @POST("upload/image")
    fun postUploadImage(@Part image: MultipartBody.Part): Call<ResponseWrapper<UploadSuccessReponse>>

    @POST("sync")
    fun postSync(@Body dto: TripSyncDTO): Call<ResponseWrapper<Any>>
}