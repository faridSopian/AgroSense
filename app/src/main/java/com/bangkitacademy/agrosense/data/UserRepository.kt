package com.bangkitacademy.agrosense.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkitacademy.agrosense.data.pref.UserModel
import com.bangkitacademy.agrosense.data.pref.UserPreference
import com.bangkitacademy.agrosense.data.remote.response.LoginResponse
import com.bangkitacademy.agrosense.data.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow
import com.bangkitacademy.agrosense.data.remote.result.Result
import com.bangkitacademy.agrosense.data.remote.retrofit.ApiService

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun login(email: String, password: String) : LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val result = apiService.login(email, password)
            emit(Result.Success(result))
        }catch (e: Exception)
        {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun register(name: String, email: String, password: String) : LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try{
            val result = apiService.register(name, email, password)
            emit(Result.Success(result))
        }catch (e : Exception)
        {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }

    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}