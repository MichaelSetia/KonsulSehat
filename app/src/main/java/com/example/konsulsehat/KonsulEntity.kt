package com.example.konsulsehat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "konsul", foreignKeys = [
    ForeignKey(entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["id_psikiater"],
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["id_pasien"],
        onDelete = ForeignKey.CASCADE)
])
data class KonsulEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int?,
    @ColumnInfo(name = "id_psikiater") val idPsikiater: Int,
    @ColumnInfo(name = "id_pasien") val idPasien: Int,
    @ColumnInfo(name = "start") val start: Date,
    @ColumnInfo(name = "end") val end: Date,
    @ColumnInfo(name = "harga") val harga: Int
)

//start = Date(),
//end = Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000), <-cnth 1 hari