package com.example.konsulsehat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatDao {
    @Insert
    suspend fun insert(chat: ChatEntity)

    @Query("SELECT * FROM chats WHERE sender_id = :userId OR receiver_id = :userId ORDER BY timestamp ASC")
    suspend fun getChatsForUser(userId: Int): List<ChatEntity>
}