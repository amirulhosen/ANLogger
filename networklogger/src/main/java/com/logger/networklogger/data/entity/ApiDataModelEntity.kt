package com.logger.networklogger.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class ApiDataModelEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var uid: Long = 0,
    @ColumnInfo(name = "url") var url: String? = null,
    @ColumnInfo(name = "request_header") var request_header: String? = null,
    @ColumnInfo(name = "request_body") var request_body: String? = null,
    @ColumnInfo(name = "request_method") var request_method: String? = null,
    @ColumnInfo(name = "response_body") var response_body: String? = null,
    @ColumnInfo(name = "status_code") var status_code: String? = null,
    @ColumnInfo(name = "request_time") var request_time: Long? = null,
    @ColumnInfo(name = "response_time") var response_time: Long? = null,
    @ColumnInfo(name = "protocol") var protocol: String? = null,
    @ColumnInfo(name = "is_ssl") var is_ssl: String? = null,
    @ColumnInfo(name = "resquest_size") var resquest_size: String? = null,
    @ColumnInfo(name = "response_size") var response_size: String? = null,
    @ColumnInfo(name = "cipher_suite") var cipher_suite: String? = null,
    @ColumnInfo(name = "response_header") var response_header: String? = null,
    @ColumnInfo(name = "tls_version") var tls_version: String? = null
) {

    @Ignore
    constructor() : this(
        url = null,
        request_header = null,
        request_body = null,
        request_method = null,
        response_body = null,
        status_code = null,
        request_time = null,
        response_time = null,
        protocol = null,
        is_ssl = null,
        resquest_size = null,
        response_size = null,
        cipher_suite = null,
        response_header = null,
        tls_version = null
    )
}