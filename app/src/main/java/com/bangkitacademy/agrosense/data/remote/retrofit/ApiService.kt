package com.bangkitacademy.agrosense.data.remote.retrofit

import com.bangkitacademy.agrosense.data.remote.response.Example
import com.bangkitacademy.agrosense.data.remote.response.LoginResponse
import com.bangkitacademy.agrosense.data.remote.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("forecast")
    fun getWeatherData(@Query("q") city: String,
                       @Query("appid") appid: String): Call<Example>
}