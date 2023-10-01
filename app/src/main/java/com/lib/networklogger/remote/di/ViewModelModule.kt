package com.lib.networklogger.remote.di

import com.lib.networklogger.remote.ui.ProductsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        ProductsViewModel(get())
    }
}
