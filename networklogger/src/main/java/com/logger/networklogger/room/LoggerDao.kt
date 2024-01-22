package com.logger.networklogger.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.logger.networklogger.data.entity.ApiDataModelEntity

@Dao
interface LoggerDao {
    @Insert
    fun insertAll(logger: ApiDataModelEntity):Long?

    @Query("SELECT * FROM apiDataModelEntity ORDER BY request_time DESC")
    fun getAll(): List<ApiDataModelEntity>

    @Query("DELETE FROM ApiDataModelEntity")
    fun delete()

    @Query("DELETE FROM apiDataModelEntity WHERE request_time <= date('now','-2 day')")
    fun deleteWith2DaysIntervalInterval()

    @Query("DELETE FROM apiDataModelEntity WHERE request_time <= date('now','-1 day')")
    fun deleteWith1DaysIntervalInterval()

    @Query("DELETE FROM apiDataModelEntity WHERE request_time <= date('now','-1 hour')")
    fun deleteWith1HourIntervalInterval()

    @Query("SELECT * FROM apiDataModelEntity WHERE uid = :id")
    fun getRow(id: Long): ApiDataModelEntity

    @Query("DELETE FROM apiDataModelEntity WHERE uid = :id")
    fun deleteUponId(id: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg logger: ApiDataModelEntity)

    @Query("SELECT uid FROM apiDataModelEntity ORDER BY uid DESC LIMIT 1")
    fun getLatestId(): Int

    @Query("SELECT request_time FROM apiDataModelEntity ORDER BY request_time ASC LIMIT 1")
    fun getLatestTime(): Long

}