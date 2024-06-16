package com.example.kessekolah.ui.core.profile.editProfile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class EditProfileViewModel: ViewModel() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val storage = FirebaseStorage.getInstance()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading


    fun editProfile(uid: String, newName: String, newProfileImageUri: Uri?, callback: (Boolean, String?) -> Unit) {
        val userRef = database.child(uid)

        if (newProfileImageUri != null) {
            Log.i("URI profile", "not null")
            val storageRef = storage.reference.child("profile_images/$uid.jpg")
            storageRef.putFile(newProfileImageUri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        updateUserProfile(uid, newName, downloadUri.toString(), callback)
                        Log.i("Edit Profile", "suskses to save profile")
                    } else {
                        callback(false, task.exception?.message)
                        Log.i("Edit Profile", "failed to save profile")
                    }
                }
        } else {
            Log.i("URI profile", "not null")
            updateUserProfile(uid, newName, null, callback)
        }
    }

    private fun updateUserProfile(uid: String, newName: String, profileImageUrl: String?, callback: (Boolean, String?) -> Unit) {
        val updates = hashMapOf<String, Any>(
            "name" to newName
        )

        profileImageUrl?.let {
            updates["userProfilePicture"] = it
        }

        database.child(uid).updateChildren(updates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
}
