package com.lib.networklogger.remote.data.repository

import cleanarch.Response
import com.lib.networklogger.remote.LibServiceApi
import com.lib.networklogger.remote.data.SampleResponse
import com.lib.networklogger.remote.domain.repository.NetworkLoggerRepository

class NetworkLoggerRepositoryImpl(private val service: LibServiceApi) : NetworkLoggerRepository {
    override suspend fun getProducts(): Response<SampleResponse> {
        return try {
            val rawResponse = service.getConfig()
            Response.Success(rawResponse)
        } catch (e: java.lang.Exception) {
            Response.Error(e)
        }
    }
}
