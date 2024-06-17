package com.bangkitacademy.agrosense.data.remote.retrofit

import com.bangkitacademy.agrosense.data.remote.response.Example
import com.bangkitacademy.agrosense.data.remote.response.LoginRequest
import com.bangkitacademy.agrosense.data.remote.response.LoginResponse
import com.bangkitacademy.agrosense.data.remote.response.RegisterRequest
import com.bangkitacademy.agrosense.data.remote.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("api/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("forecast")
    fun getWeatherData(@Query("q") city: String,
                       @Query("appid") appid: String): Call<Example>
}