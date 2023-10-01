package com.lib.networklogger.remote.di

import com.lib.networklogger.remote.domain.interactor.GetProductsUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetProductsUseCase(get()) }
}
