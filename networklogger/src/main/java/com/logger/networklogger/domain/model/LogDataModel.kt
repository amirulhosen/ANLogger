package com.logger.networklogger.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class LogDataModel(
    @SerializedName("logs")
    val logs: List<Log>
) {
    @Keep
    data class Log(
        @SerializedName("uid")
        val uid: Long,
        @SerializedName("request_body")
        val requestBody: String,
        @SerializedName("request_header")
        val requestHeader: String,
        @SerializedName("request_method")
        val requestMethod: String,
        @SerializedName("response_body")
        val responseBody: String,
        @SerializedName("response_header")
        val responseHeader: String,
        @SerializedName("url")
        val url: String,
        @SerializedName("request_time")
        val request_time: Long,
        @SerializedName("status_code")
        val statusCode: String,
        @SerializedName("response_time")
        val response_time: Long,
        @SerializedName("protocol")
        val protocol: String,
        @SerializedName("is_ssl")
        val is_ssl: String,
        @SerializedName("resquest_size")
        val resquest_size: String,
        @SerializedName("response_size")
        val response_size: String,
        @SerializedName("tls_version")
        val tls_version: String,
        @SerializedName("cipher_suite")
        val cipher_suite: String
    )
}