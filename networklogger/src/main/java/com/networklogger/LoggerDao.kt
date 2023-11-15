package com.networklogger

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LoggerDao {
    @Insert
    fun insertAll(vararg logger: ApiDataModelEntity)

    @Query("SELECT * FROM apiDataModelEntity")
    fun getAll(): List<ApiDataModelEntity>

    @Delete
    fun delete(logger: ApiDataModelEntity)
}