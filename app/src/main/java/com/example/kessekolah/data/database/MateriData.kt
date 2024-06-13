package com.example.kessekolah.data.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MateriData(
    val judul: String,
    val tahun: String,
    val category: String,
    val fileName: String,
    val fileUrl: String,
    val timestamp: String,
    val uid: String,
    val dataIlus: Int,
    val backColorBanner: String,
) : Parcelable
