package com.example.kessekolah.model

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.User
import com.example.kessekolah.data.repo.SignUpRepository

class CompleteProfileViewModel(application: Application) : ViewModel() {

    private val mSignUpRepository: SignUpRepository = SignUpRepository(application)

    fun insertUser(user: User) {
        mSignUpRepository.signUpUser(user)
    }

    fun checkEmailExists(phoneNumber: String, callback: (Boolean) -> Unit) {
        mSignUpRepository.checkEmailExists(phoneNumber) { exists ->
            callback(exists)
        }
    }
}