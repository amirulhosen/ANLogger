package com.logger.networklogger.data

import android.content.Context
import android.database.Cursor
import android.os.Environment
import android.widget.Toast
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.logger.AppDatabase
import com.logger.networklogger.data.entity.ApiDataModelEntity
import com.logger.networklogger.domain.model.LogDataModel
import com.logger.networklogger.domain.model.LogDataSharableModel
import com.logger.networklogger.room.LoggerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type


class RoomDbRepositoryImpl(val context: Context) : RoomDbRepository {

    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "networkLogger"

    ).build()
    private var loggerDao: LoggerDao? = database.networkDao()

    override suspend fun insertData(apiDataModelEntity: ApiDataModelEntity) {
        val uid = loggerDao?.insertAll(apiDataModelEntity)
        apiDataModelEntity.uid = uid ?: 0
    }

    override suspend fun deleteTable() {
        withContext(Dispatchers.IO) {
            loggerDao?.delete()
        }
    }

    override suspend fun getRow(rowId: Long): LogDataModel.Log {
        return withContext(Dispatchers.IO) {
            val result = loggerDao?.getRow(rowId)
            return@withContext LogDataModel.Log(
                uid = result?.uid ?: 0,
                requestBody = result?.request_body.orEmpty(),
                requestHeader = result?.request_header.orEmpty(),
                requestMethod = result?.request_method.orEmpty(),
                responseBody = result?.response_body.orEmpty(),
                responseHeader = result?.response_header.orEmpty(),
                url = result?.url.orEmpty(),
                request_time = result?.request_time ?: 0,
                statusCode = result?.status_code.orEmpty(),
                response_time = result?.response_time ?: 0,
                protocol = result?.protocol.orEmpty(),
                is_ssl = result?.is_ssl.orEmpty(),
                resquest_size = result?.resquest_size.orEmpty(),
                response_size = result?.response_size.orEmpty(),
                tls_version = result?.tls_version.orEmpty(),
                cipher_suite = result?.cipher_suite.orEmpty(),
            )
        }
    }

    override suspend fun updateTable(apiDataModelEntity: ApiDataModelEntity) {
        loggerDao?.update(apiDataModelEntity)
    }

    override suspend fun getLatestId(): Int {
        return loggerDao?.getLatestId() ?: -1
    }

    override suspend fun deleteRowBasedOnId(uid: Long) {
        loggerDao?.deleteUponId(uid)
    }

    override suspend fun getFullLog(): List<LogDataModel.Log> {
        return withContext(Dispatchers.IO) {
            return@withContext (loggerDao?.getAll() ?: listOf()).map {
                LogDataModel.Log(
                    uid = it.uid,
                    requestBody = it.request_body.orEmpty(),
                    requestHeader = it.request_header.orEmpty(),
                    requestMethod = it.request_method.orEmpty(),
                    responseBody = it.response_body.orEmpty(),
                    responseHeader = it.response_header.orEmpty(),
                    url = it.url.orEmpty(),
                    request_time = it.request_time ?: 0,
                    statusCode = it.status_code.orEmpty(),
                    response_time = it.response_time ?: 0,
                    protocol = it.protocol.orEmpty(),
                    is_ssl = it.is_ssl.orEmpty(),
                    resquest_size = it.resquest_size.orEmpty(),
                    response_size = it.response_size.orEmpty(),
                    tls_version = it.tls_version.orEmpty(),
                    cipher_suite = it.cipher_suite.orEmpty()
                )
            }
        }
    }

    override suspend fun getFullLogJson(): File {
        return withContext(Dispatchers.IO) {
            try {
                val result = loggerDao?.getAll()?.map {
                    LogDataSharableModel(
                        requestBody = it.request_body.orEmpty(),
                        requestHeader = it.request_header.orEmpty(),
                        requestMethod = it.request_method.orEmpty(),
                        responseBody = it.response_body.orEmpty(),
                        responseHeader = it.response_header.orEmpty(),
                        url = it.url.orEmpty(),
                        request_time = it.request_time ?: 0,
                        statusCode = it.status_code.orEmpty(),
                        response_time = it.response_time ?: 0,
                        protocol = it.protocol.orEmpty(),
                        is_ssl = it.is_ssl.orEmpty(),
                        resquest_size = it.resquest_size.orEmpty(),
                        response_size = it.response_size.orEmpty(),
                        tls_version = it.tls_version.orEmpty(),
                        cipher_suite = it.cipher_suite.orEmpty()
                    )
                }
                val gson = Gson()
                val type: Type = object : TypeToken<List<LogDataSharableModel>>() {}.type
                val jsonString = gson.toJson(result, type)
                writeToFile(jsonString, context)
            } catch (e: Exception) {
                return@withContext File(
                    (context.getExternalFilesDir("")?.absolutePath ?: "") + "/Logs",
                    "networkLogg.txt"
                )
            }
        }
    }

    override suspend fun getSingleLogJson(log: LogDataModel.Log): File {
        return withContext(Dispatchers.IO) {
            try {
                val logDataSharableModel = LogDataSharableModel(
                    requestBody = log.requestBody,
                    requestHeader = log.requestHeader,
                    requestMethod = log.requestMethod,
                    responseBody = log.responseBody,
                    responseHeader = log.responseHeader,
                    url = log.url,
                    request_time = log.request_time,
                    statusCode = log.statusCode,
                    response_time = log.response_time,
                    protocol = log.protocol,
                    is_ssl = log.is_ssl,
                    resquest_size = log.resquest_size,
                    response_size = log.response_size,
                    tls_version = log.tls_version,
                    cipher_suite = log.cipher_suite
                )
                val gson = Gson()
                val type: Type = object : TypeToken<LogDataSharableModel>() {}.type
                val jsonString = gson.toJson(logDataSharableModel, type)
                writeToFile(jsonString, context)
            } catch (e: Exception) {
                return@withContext File(
                    (context.getExternalFilesDir("")?.absolutePath ?: "") + "/Logs",
                    "networkLogg.txt"
                )
            }
        }
    }

    override suspend fun getLastInsertedTime(): Long {
        return withContext(Dispatchers.IO) {
            return@withContext loggerDao?.getLatestTime() ?: 0
        }
    }

    override suspend fun exportToSQLite(): File {
        val fileDirectory = File(
            context.cacheDir.path + "/Logs"
        )
        val file = File(
            context.cacheDir.path + "/Logs",
            "networkLog.sqlite"
        )
        return withContext(Dispatchers.IO) {
            return@withContext if (fileDirectory.canWrite()) {
                val currentDBPath =
                    AppDatabase.getInstance(context)?.openHelper?.writableDatabase?.path
                val backupDBPath =
                    "networkLog.sqlite"      //you can modify the file type you need to export
                val currentDB = File(currentDBPath)
                val backupDB = File(fileDirectory, backupDBPath)
                if (currentDB.exists()) {
                    try {
                        val src = FileInputStream(currentDB).channel
                        val dst = FileOutputStream(backupDB).channel
                        dst.transferFrom(src, 0, src.size())
                        src.close()
                        dst.close()
                        file
                    } catch (e: IOException) {
                        file
                    }
                } else {
                    file
                }
            } else {
                file
            }
        }
    }

    private fun writeToFile(data: String, context: Context): File {
        try {
            val parentDirectory = File(context.cacheDir.path+"/Logs")
            val file = File(
                context.cacheDir.path + "/Logs",
                "networkLog.txt"
            )
            if (!parentDirectory.exists()) {
                parentDirectory.mkdir()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
            val fileWriter = FileWriter(file, false)
            fileWriter.write(data)
            fileWriter.flush()
            fileWriter.close()
            return file
        } catch (e: IOException) {
            return File(
                (context.getExternalFilesDir("")?.absolutePath ?: "") + "/Logs",
                "networkLog.txt"
            )
        }
    }
}