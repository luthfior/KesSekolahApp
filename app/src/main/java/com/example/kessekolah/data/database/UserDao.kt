package com.example.kessekolah.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user: User)

    @Update
    fun update(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password OR email = :email AND password = :password")
    fun getUserLogin(username: String, email: String, password: String): LiveData<User>

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE username = :username OR email = :email")
    fun getUserByUsernameAndEmail(username: String, email: String): LiveData<User?>

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber")
    fun getUserByPhoneNumber(phoneNumber: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmail(email: String): User?

}