package com.lib.networklogger.remote

import android.app.Application
import com.lib.networklogger.remote.di.repositoryModule
import com.lib.networklogger.remote.di.serviceModule
import com.lib.networklogger.remote.di.useCaseModule
import com.lib.networklogger.remote.di.viewModelModule
import com.logger.di.loggerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        loadKoin()
    }
    private fun loadKoin() {
        startKoin {
            androidContext(this@MainApplication)
            modules(
                listOf(
                    serviceModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule,
                    loggerModule
                )
            )
        }
    }
}