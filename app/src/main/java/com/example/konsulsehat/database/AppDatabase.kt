package com.example.konsulsehat.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UserEntity::class, ChatEntity::class, KonsulEntity::class ], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase:RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun userDao(): UserDao
    abstract fun konsulDao(): KonsulDao
    companion object {
        private var _database: AppDatabase? = null

        fun build(context: Context?): AppDatabase {
            if(_database == null){
                _database = Room.databaseBuilder(context!!, AppDatabase::class.java,"konsul_database").fallbackToDestructiveMigration().build()

            }
            return _database!!
        }
    }
}