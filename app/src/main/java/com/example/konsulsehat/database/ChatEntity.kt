package com.example.konsulsehat.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "chats", foreignKeys = [
    ForeignKey(entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["sender_id"],
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["receiver_id"],
        onDelete = ForeignKey.CASCADE)
])
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int?,
    @ColumnInfo(name = "sender_id") val senderId: Int,
    @ColumnInfo(name = "receiver_id") val receiverId: Int,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long


//    val chatMessage = ChatEntity(senderId = sender.id, receiverId = receiver.id, message = "Hello, World!", timestamp = System.currentTimeMillis()) <- ngisi Timestamp
)