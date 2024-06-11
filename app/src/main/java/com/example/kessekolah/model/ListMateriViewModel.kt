package com.example.kessekolah.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kessekolah.data.database.MateriList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


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
                    val fileName = fileSnapshot.child("fileName").getValue(String::class.java) ?: ""
                    val judul = fileSnapshot.child("judul").getValue(String::class.java) ?: ""
                    val timeStamp =
                        fileSnapshot.child("timestamp").getValue(String::class.java) ?: ""
                    val tahun = fileSnapshot.child("tahun").getValue(String::class.java) ?: ""
                    val fileUrl = fileSnapshot.child("fileUrl").getValue(String::class.java) ?: ""
                    val dataIcon = fileSnapshot.child("dataIlus").getValue(Int::class.java) ?: 0
                    val materi = MateriList(
                        fileName = fileName,
                        title = judul,
                        fileUrl = fileUrl,
                        tahun = tahun,
                        category = "Materi",
                        timeStamp = timeStamp,
                        icon = dataIcon
                    )
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

    fun deleteMateri(data: MateriList) {
        _loading.value = true
        val materiQuery: Query = materiRef.orderByChild("judul").equalTo(data.title)

        materiQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (materiSnapshot in dataSnapshot.children) {
                    val fileUrl = materiSnapshot.child("fileUrl").getValue(String::class.java)
                    materiSnapshot.ref.removeValue()

                    fileUrl?.let {
                        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(it)
                        storageRef.delete().addOnSuccessListener {

                            Log.d("ViewModelListMateri", "File deleted successfully")
                        }.addOnFailureListener { exception ->

                            Log.e("ViewModelListMateri", "Error deleting file", exception)
                        }
                    }
                }
                _loading.value = false
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _loading.value = false
                Log.e("ViewModelListMateri", "onCancelled", databaseError.toException())
            }
        })
    }
}
