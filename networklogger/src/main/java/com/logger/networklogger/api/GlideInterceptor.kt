package com.logger.networklogger.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class GlideInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val newRequest: Request
        newRequest = request.newBuilder()
            .addHeader("type", "glide")
            .build()
        return chain.proceed(newRequest)
    }
}