package com.example.kessekolah.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginData(
    var token: String?,
    var username: String?,
    var nama: String?,
    var email: String?,
    var phoneNumber: String?
):Parcelable
