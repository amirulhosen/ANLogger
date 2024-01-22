package com.logger.networklogger.api

import android.content.Context
import android.content.Intent
import com.logger.networklogger.ClearDatabaseService
import com.logger.networklogger.data.entity.ApiDataModelEntity
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.connection.RealCall
import okhttp3.internal.http.RealInterceptorChain
import org.koin.java.KoinJavaComponent.inject


class RequestLoggingInterceptor(val context: Context, val useSqlite: Boolean) : Interceptor {

    private val roomDbHelper: RoomDbHelper by inject(RoomDbHelper::class.java)
    private val fileHelper: FileHelper by inject(FileHelper::class.java)
    override fun intercept(chain: Interceptor.Chain): Response {
        SuppressApi.isSqliteEnable = useSqlite
        val request: Request = chain.request()
        val isImage = ((chain as RealInterceptorChain).call() as RealCall).client.interceptors.find { it.javaClass.name ==  GlideInterceptor::javaClass.name}!=null
        val apiDataModelEntity = ApiDataModelEntity()
        if (!SuppressApi.clearedDb) {
            SuppressApi.clearedDb = true
            val intent = Intent(context, ClearDatabaseService::class.java)
            ClearDatabaseService().enqueueWork(context, intent)
        }

        val isImageType =
            ImageExtensions.values().find { request.url.toString().endsWith(it.imageExt) } != null
        val jsonObjectPair = if (SuppressApi.apiList.isEmpty() && !isImageType && !isImage)
            roomDbHelper.saveRequestToDb(request, apiDataModelEntity)
        else if (SuppressApi.apiList.find {
                request.url.toString().contains(it)
            } != null && !isImageType && !isImage)
            roomDbHelper.saveRequestToDb(request, apiDataModelEntity)
        else
            null
        var response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            roomDbHelper.saveFailedApi(
                apiDataModelEntity,
                e.toString()
            )
            throw e
        }

        return if (!useSqlite) {
            fileHelper.saveToFile(context, request, response)
        } else {
            if (jsonObjectPair != null && response.body?.contentType().toString()
                    .contains("application/json", true)
            ) {
                try {
                    response = roomDbHelper.saveResponseToDb(
                        response,
                        apiDataModelEntity
                    )
                    showNotification(
                        context,
                        (response.code.toString() + " " + request.url),
                        request.method
                    )
                } catch (e: OutOfMemoryError) {
                    roomDbHelper.saveFailedApi(
                        apiDataModelEntity,
                        e.toString()
                    )
                }
            } else {
                roomDbHelper.deleteRow(apiDataModelEntity.uid)
            }
            response
        }
    }
}