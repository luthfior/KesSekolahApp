package com.example.kessekolah.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginData(
    val token: String?,
    val name: String?,
    val email: String?,
    val role: String?,
    val profilePicture: String?,
    val isLogin: Boolean
):Parcelable
