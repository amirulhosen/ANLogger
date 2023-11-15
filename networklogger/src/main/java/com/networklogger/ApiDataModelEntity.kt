package com.networklogger

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ApiDataModelEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "url") val url: String?,
    @ColumnInfo(name = "request_header") val request_header: String?,
    @ColumnInfo(name = "request_body") val request_body: String?,
    @ColumnInfo(name = "request_method") val request_method: String?,
    @ColumnInfo(name = "response_body") val response_body: String?,
    @ColumnInfo(name = "status_code") val status_code: String?,
    @ColumnInfo(name = "request_time") val request_time: String?,
    @ColumnInfo(name = "response_time") val response_time: String?,
    @ColumnInfo(name = "protocol") val protocol: String?,
    @ColumnInfo(name = "is_ssl") val is_ssl: String?,
    @ColumnInfo(name = "resquest_size") val resquest_size : String?,
    @ColumnInfo(name = "response_size") val response_size: String?,
    @ColumnInfo(name = "cipher_suite") val cipher_suite: String?,
    @ColumnInfo(name = "response_header") val response_header: String?
)