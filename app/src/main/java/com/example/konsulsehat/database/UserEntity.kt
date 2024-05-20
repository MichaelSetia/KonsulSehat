package com.example.konsulsehat.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id:Int?,
    @ColumnInfo(name = "username") val username:String,
    @ColumnInfo(name = "password") var password:String,
    @ColumnInfo(name = "role") var role:String,
    @ColumnInfo(name = "status") var status:String,
    @ColumnInfo(name = "pendapatan") var pendapatan:String?,
    @ColumnInfo(name = "harga") var harga:String?,
    @ColumnInfo(name = "rating") var rating:String?,
){
    override fun toString(): String {
        return "$username"
    }
}