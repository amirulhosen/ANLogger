package com.lib.networklogger.remote.domain.repository

import cleanarch.Response
import com.lib.networklogger.remote.data.SampleResponse

interface NetworkLoggerRepository {
    suspend fun getProducts(): Response<SampleResponse>
}