package com.example.konsulsehat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "dtrans", foreignKeys = [
    ForeignKey(entity = HTransEntity::class,
        parentColumns = ["htrans_id"],
        childColumns = ["htrans_id"],
        onDelete = ForeignKey.CASCADE)
])
data class DTransEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "dtrans_id") val id: Int?,
    @ColumnInfo(name = "htrans_id") val htransId: Int,
    @ColumnInfo(name = "start") val start: Date,
    @ColumnInfo(name = "end") val end: Date,
    @ColumnInfo(name = "harga") val harga: Double
)