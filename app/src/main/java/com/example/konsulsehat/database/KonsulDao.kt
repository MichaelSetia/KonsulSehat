package com.example.konsulsehat.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface KonsulDao {
    @Insert
    suspend fun insert(konsul: KonsulEntity)

    @Query("SELECT * FROM konsul WHERE id_psikiater = :psikiaterId")
    suspend fun getKonsulByPsikiater(psikiaterId: Int): List<KonsulEntity>

    @Query("SELECT * FROM konsul WHERE id_pasien = :pasienId")
    suspend fun getKonsulByPasien(pasienId: Int): List<KonsulEntity>
}