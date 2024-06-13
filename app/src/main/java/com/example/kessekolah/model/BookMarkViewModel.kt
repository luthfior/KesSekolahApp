package com.example.kessekolah.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.MateriData
import com.example.kessekolah.data.repo.MateriRepository

class BookMarkViewModel(application: Application) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val materiRepository: MateriRepository = MateriRepository(application)

    fun getAllUsers(): LiveData<List<MateriData>> = materiRepository.getAllFavorite()
}