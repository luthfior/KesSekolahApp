package com.example.kessekolah.model

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.User
import com.example.kessekolah.data.repo.SignUpRepository

class LoginViewModel(application: Application) : ViewModel() {

    private val appContext = application.applicationContext
    private val mSignUpRepository: SignUpRepository = SignUpRepository(application)

    fun loginUser(username: String, email: String, password: String, callback: (Boolean) -> Unit) {
        mSignUpRepository.getUserLogin(username, email, password).observeForever { user ->
            if (user != null) {
                if (user.password != password) {
                    Toast.makeText(appContext, "Login gagal. Password Kamu Salah", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
                callback(true)
            } else {
                Toast.makeText(appContext, "Login gagal. Akun tidak ditemukan, silahkan Buat Akun terlebih dahulu", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        }
    }

    fun getUser(username: String, email: String, password: String, callback: (Boolean) -> Unit) {
        mSignUpRepository.getUserByUsernameAndEmail(username, email).observeForever { user ->
            if (user != null && user.password == password) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun getUserByUsernameAndEmail(username: String, email: String): LiveData<User?> {
        return mSignUpRepository.getUserByUsernameAndEmail(username, email)
    }
}