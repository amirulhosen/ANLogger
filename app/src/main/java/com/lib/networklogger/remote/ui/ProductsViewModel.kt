package com.lib.networklogger.remote.ui

import androidx.lifecycle.*
import com.lib.networklogger.remote.data.SampleResponse
import com.lib.networklogger.remote.domain.interactor.GetProductsUseCase

class ProductsViewModel(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    // Inputs
    val productListRequest = MutableLiveData<Unit>()

    // outputs
    val productList: LiveData<SampleResponse> get() = _productList

    // Transformations

    private val getOperatingAccountResult: LiveData<cleanarch.Result<SampleResponse>> =
        productListRequest.switchMap {
            getProductsUseCase.execute().asLiveData()
        }

    private val _productList = MediatorLiveData<SampleResponse>()

    init {

        _productList.addSource(getOperatingAccountResult) {
            if (it is cleanarch.Result.Success) {
                _productList.postValue(it.data)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        getProductsUseCase.cancel()
    }
}