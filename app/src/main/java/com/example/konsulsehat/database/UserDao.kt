package com.example.konsulsehat.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

interface UserDao {
    @Insert
    fun insert(user: UserEntity)

    @Update
    fun update(user: UserEntity)

    @Delete
    fun delete(user: UserEntity)

    @Query("DELETE FROM users where username = :username")
    fun deleteQuery(username: String):Int //return Int jika mau tau brp row yg kehapus

    @Query("SELECT * FROM users")
    fun fetch():List<UserEntity>

    @Query("SELECT * FROM users where username = :username")
    fun get(username:String): UserEntity?

    @Query("SELECT username FROM users where id = :id")
    fun getUsername(id: Int): String?

    @Query("SELECT * FROM users WHERE id = :userId ")
    fun getOwnerById(userId: Int): UserEntity?

    @Query("SELECT id FROM users ORDER BY id DESC LIMIT 1")
    fun getLastInsertedUserId(): Int?
}