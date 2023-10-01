package com.lib.networklogger.remote.domain.interactor

import cleanarch.Response
import cleanarch.UseCaseScope
import com.lib.networklogger.remote.data.SampleResponse
import com.lib.networklogger.remote.domain.repository.NetworkLoggerRepository
import com.scb.cleanarch.components.UseCase
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetProductsUseCase(private val accountsRepository: NetworkLoggerRepository) :
    UseCase<Flow<cleanarch.Result<SampleResponse>>>,
    UseCaseScope {

    override fun execute(): Flow<cleanarch.Result<SampleResponse>> {
        return flow {
            // Subsequent paging request
            val response = accountsRepository.getProducts()
            if (response is Response.Success) {
                emit(cleanarch.Result.Success(response.data))
            } else if (response is Response.Error) {
                emit(cleanarch.Result.Error(response.exception))
            }
        }
    }

    override fun cancel() {
        coroutineContext.cancel()
    }
}