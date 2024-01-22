package com.logger

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.logger.networklogger.data.entity.ApiDataModelEntity
import com.logger.networklogger.room.LoggerDao

@Database(
    entities = [ApiDataModelEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun networkDao(): LoggerDao?

    companion object {
        private val LOCK = Any()
        private const val DATABASE_NAME = "networkLogger"
        private var sInstance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance =
                        Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, DATABASE_NAME
                        )
                            .build()
                }
            }
            return sInstance
        }
    }
}