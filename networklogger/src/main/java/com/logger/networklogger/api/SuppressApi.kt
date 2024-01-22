package com.logger.networklogger.api

object SuppressApi {
    var suppressApi:List<Pair<String,String>> = emptyList()
    var apiList:List<String> = emptyList()
    internal var isSqliteEnable:Boolean = true
    internal var clearedDb:Boolean = false
    internal var clearedTimberDb:Boolean = false
}