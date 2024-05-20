package com.example.konsulsehat.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "htrans", foreignKeys = [
    ForeignKey(entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["id_psikiater"],
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["id_pasien"],
        onDelete = ForeignKey.CASCADE)
])
data class HTransEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "htrans_id") val id: Int?,
    @ColumnInfo(name = "id_psikiater") val idPsikiater: Int,
    @ColumnInfo(name = "id_pasien") val idPasien: Int,
    @ColumnInfo(name = "tanggal") val tanggal: Date,
    @ColumnInfo(name = "total_harga") val totalHarga: Double
)