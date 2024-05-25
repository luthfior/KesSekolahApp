package com.example.kessekolah.utils

import android.content.Context
import com.example.kessekolah.data.remote.LoginData

class LoginPreference(context: Context) {

    private val preference = context.getSharedPreferences("login", Context.MODE_PRIVATE)

    fun saveData(data: LoginData) {
        val editor = preference.edit()
        editor.putString("token", data.token)
        editor.putString("username", data.username)
        editor.putString("nama", data.nama)
        editor.putString("email", data.email)
        editor.putString("phoneNumber", data.phoneNumber)
        editor.apply()
    }

    fun getData(): LoginData {
        val token = preference.getString("token", "")
        val username = preference.getString("username", "")
        val nama = preference.getString("nama", "")
        val email = preference.getString("email", "")
        val phoneNumber = preference.getString("phoneNumber", "")

        return LoginData(token, username, nama, email, phoneNumber)
    }

    fun removeData() {
        var editor = preference.edit()
        editor.remove("token")
        editor.remove("username")
        editor.remove("nama")
        editor.remove("email")
        editor.remove("phoneNumber")
        editor.apply()
    }

}