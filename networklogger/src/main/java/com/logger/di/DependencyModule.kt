package com.logger.di

import com.logger.networklogger.api.FileHelper
import com.logger.networklogger.api.RoomDbHelper
import com.logger.networklogger.data.RoomDbRepository
import com.logger.networklogger.data.RoomDbRepositoryImpl
import com.logger.networklogger.domain.interactor.DeleteAllLogUseCase
import com.logger.networklogger.domain.interactor.GetAllLogUseCase
import com.logger.networklogger.domain.interactor.GetSingleLogJsonUseCase
import com.logger.networklogger.domain.interactor.ShareLogUseCase
import com.logger.networklogger.ui.details.LogDetailsViewmodel
import com.logger.networklogger.ui.list.LogListViewmodel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val loggerModule = module {
    single<RoomDbRepository> { RoomDbRepositoryImpl(androidApplication()) }
    factory { RoomDbHelper() }
    factory { FileHelper() }
    factory { GetAllLogUseCase(get()) }
    factory { DeleteAllLogUseCase(get()) }
    factory { GetSingleLogJsonUseCase(get()) }
    factory { ShareLogUseCase(get()) }
    viewModel { LogListViewmodel(get(), get(), get()) }
    viewModel { LogDetailsViewmodel(get()) }
}