package com.logger.networklogger.data

import com.logger.networklogger.data.entity.ApiDataModelEntity
import com.logger.networklogger.domain.model.LogDataModel
import java.io.File

interface RoomDbRepository {
    suspend fun insertData(apiDataModelEntity: ApiDataModelEntity)
    suspend fun deleteTable()
    suspend fun getRow(rowId: Long): LogDataModel.Log
    suspend fun updateTable(apiDataModelEntity: ApiDataModelEntity)
    suspend fun getLatestId(): Int
    suspend fun deleteRowBasedOnId(uid: Long)
    suspend fun getFullLog(): List<LogDataModel.Log>
    suspend fun getFullLogJson(): File
    suspend fun getSingleLogJson(log: LogDataModel.Log): File
    suspend fun getLastInsertedTime(): Long
    suspend fun exportToSQLite(): File
}