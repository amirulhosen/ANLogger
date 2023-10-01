package com.lib.networklogger.remote.di

import com.lib.networklogger.remote.data.repository.NetworkLoggerRepositoryImpl
import com.lib.networklogger.remote.domain.repository.NetworkLoggerRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { NetworkLoggerRepositoryImpl(get()) as NetworkLoggerRepository }
}
