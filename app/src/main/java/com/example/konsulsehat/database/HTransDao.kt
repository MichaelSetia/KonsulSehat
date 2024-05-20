package com.example.konsulsehat.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HTransDao {
    @Insert
    suspend fun insert(htrans: HTransEntity)

    @Query("SELECT * FROM htrans WHERE htrans_id = :id")
    suspend fun getHTransById(id: Int): HTransEntity
}

