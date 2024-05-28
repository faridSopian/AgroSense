package com.bangkitacademy.agrosense.view.signup

import androidx.lifecycle.ViewModel
import com.bangkitacademy.agrosense.data.UserRepository

class SignupViewModel(private val repo: UserRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) = repo.register(name, email, password)
}