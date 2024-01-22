package com.logger.networklogger.domain.model

enum class ShareType(val type: Int) {
    JSON(0),
    CSV(1),
    SQLITE(2)
}