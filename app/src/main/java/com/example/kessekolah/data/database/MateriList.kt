package com.example.kessekolah.data.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MateriList(
    val fileName: String,
    val title: String,
    val fileUrl: String,
    val tahun: String,
    val category: String,
    val timeStamp: String,
    val icon: Int
) : Parcelable
