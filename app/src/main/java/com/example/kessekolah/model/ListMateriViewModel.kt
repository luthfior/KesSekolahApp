package com.example.kessekolah.model

import android.util.Log
import com.example.kessekolah.R
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.MateriList
import com.google.firebase.database.*

class ListMateriViewModel : ViewModel() {
    private val materiRef = FirebaseDatabase.getInstance().getReference("materi")
    private val _materiList = MutableLiveData<List<MateriList>>()
    val materiList: LiveData<List<MateriList>> = _materiList
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        fetchMateriList()
    }

    private fun fetchMateriList() {
        _loading.value = true
        materiRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<MateriList>()
                for (fileSnapshot in snapshot.children) {
                    val judul = fileSnapshot.child("judul").getValue(String::class.java) ?: ""
                    val timeStamp = fileSnapshot.child("timestamp").getValue(String::class.java) ?: ""
                    val materi = MateriList(judul, "Materi", timeStamp, R.drawable.menu_materi_icon)
                    list.add(materi)
                }
                _materiList.value = list
                _loading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ListMateriFragment", "Failed to read database", error.toException())
            }
        })
    }
}
