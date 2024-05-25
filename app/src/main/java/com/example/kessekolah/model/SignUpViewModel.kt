package com.example.kessekolah.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.User
import com.example.kessekolah.data.repo.SignUpRepository
import com.example.kessekolah.utils.Resource

class SignUpViewModel(application: Application) : ViewModel() {

    private val mSignUpRepository: SignUpRepository = SignUpRepository(application)

    fun insertUser(user: User) {
        mSignUpRepository.signUpUser(user)
    }

    fun checkUsernameExists(username: String, callback: (Boolean) -> Unit) {
        mSignUpRepository.checkUsernameExists(username) { exists ->
            callback(exists)
        }
    }

    fun checkPhoneNumberExists(phoneNumber: String, callback: (Boolean) -> Unit) {
        mSignUpRepository.checkPhoneNumberExists(phoneNumber) { exists ->
            callback(exists)
        }
    }
}
