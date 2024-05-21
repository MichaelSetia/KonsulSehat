package com.example.konsulsehat.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DTransDao {
    @Dao
    interface DTransDao {
        @Insert
        suspend fun insert(dtrans: DTransEntity)

        @Query("SELECT * FROM dtrans WHERE htrans_id = :htransId")
        suspend fun getDTransByHTransId(htransId: Int): List<DTransEntity>

        @Transaction
        @Query("SELECT * FROM dtrans WHERE htrans_id = :htransId")
        suspend fun getHTransWithDTrans(htransId: Int): List<DTransEntity>
    }
}