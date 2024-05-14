package com.example.kessekolah.data.repo

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.kessekolah.data.database.User
import com.example.kessekolah.data.database.UserDao
import com.example.kessekolah.data.database.UserRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SignUpRepository(application: Application) {
    private val mUserDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = UserRoomDatabase.getDatabase(application)
        mUserDao = db.userDao()
    }

    fun signUpUser(user: User) {
        executorService.execute {mUserDao.insertUser(user)}
    }

    fun updateUser(user: User) {
        executorService.execute { mUserDao.update(user) }
    }

    fun getUserLogin(username: String, email: String, password: String): LiveData<User> = mUserDao.getUserLogin(username, email, password)
    fun checkUsernameExists(username: String, callback: (Boolean) -> Unit) {
        executorService.execute {
            val existingUser = mUserDao.getUserByUsername(username)
            callback(existingUser != null)
        }
    }

    fun checkPhoneNumberExists(phoneNumber: String, callback: (Boolean) -> Unit) {
        executorService.execute {
            val existingUser = mUserDao.getUserByPhoneNumber(phoneNumber)
            callback(existingUser != null)
        }
    }

    fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        executorService.execute {
            val existingUser = mUserDao.getUserByEmail(email)
            callback(existingUser != null)
        }
    }

    fun getUserByUsernameAndEmail(username: String, email: String): LiveData<User?> {
        return mUserDao.getUserByUsernameAndEmail(username, email)
    }

}