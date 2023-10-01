package com.lib.networklogger.remote

import com.lib.networklogger.remote.data.SampleResponse
import retrofit2.http.*

@Suppress("TooManyFunctions")
interface LibServiceApi {

    @GET("/products/1")
    suspend fun getConfig(): SampleResponse
}
