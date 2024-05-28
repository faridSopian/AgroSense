package com.bangkitacademy.agrosense.di

import android.content.Context
import com.bangkitacademy.agrosense.data.UserRepository
import com.bangkitacademy.agrosense.data.pref.UserPreference
import com.bangkitacademy.agrosense.data.pref.dataStore
import com.bangkitacademy.agrosense.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }
}