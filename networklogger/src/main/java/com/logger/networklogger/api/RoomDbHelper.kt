package com.logger.networklogger.api

import com.logger.networklogger.data.RoomDbRepository
import com.logger.networklogger.data.entity.ApiDataModelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.koin.java.KoinJavaComponent

class RoomDbHelper {
    private val roomDbRepository: RoomDbRepository by KoinJavaComponent.inject(RoomDbRepository::class.java)
    private val scope = MainScope()
    internal fun saveRequestToDb(
        request: Request,
        apiDataModelEntity: ApiDataModelEntity
    ): ApiDataModelEntity {
        scope.launch {
            withContext(Dispatchers.IO) {
                apiDataModelEntity.is_ssl = if (request.isHttps) "Yes" else "No"
                apiDataModelEntity.resquest_size = request.body?.contentLength().toString()
                apiDataModelEntity.url = request.url.toString()
                apiDataModelEntity.request_header = request.headers.toString()
                apiDataModelEntity.request_method = request.method
                apiDataModelEntity.request_time = System.currentTimeMillis()
                apiDataModelEntity.request_body =
                    suppressResponseIfneeded(request, apiDataModelEntity).request_body
                roomDbRepository.insertData(apiDataModelEntity)
            }
        }
        return apiDataModelEntity
    }

    internal fun saveResponseToDb(
        response: Response,
        apiDataModelEntity: ApiDataModelEntity,
    ): Response {
        var updatedResponse: Response = response
        val responseBody = response.body
        if (responseBody != null) {
            val responseBodyString = responseBody.string()
            updatedResponse = response.newBuilder()
                .body(
                    ResponseBody.create(
                        responseBody.contentType(),
                        responseBodyString.toByteArray()
                    )
                )
                .build()


            apiDataModelEntity.apply {
                request_time = response.sentRequestAtMillis
                status_code = response.code.toString()
                response_body = responseBodyString
                response_time = response.receivedResponseAtMillis
                protocol = response.protocol.toString()
                response_size = response.body?.contentLength().toString()
                tls_version = response.handshake?.tlsVersion.toString()
                cipher_suite = response.handshake?.cipherSuite.toString()
                response_header = response.headers.toString()
            }

            scope.launch {
                withContext(Dispatchers.IO) {
                    roomDbRepository.updateTable(apiDataModelEntity)
                }
            }
        }
        return updatedResponse
    }

    fun saveFailedApi(apiDataModelEntity: ApiDataModelEntity, failReason: String) {
        apiDataModelEntity.response_body = failReason
        scope.launch {
            withContext(Dispatchers.IO) {
                roomDbRepository.updateTable(apiDataModelEntity)
            }
        }
    }

    internal fun deleteRow(uid: Long) {
        scope.launch {
            withContext(Dispatchers.IO) {
                roomDbRepository.deleteRowBasedOnId(uid)
            }
        }
    }

}