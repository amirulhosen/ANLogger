package com.logger.networklogger.domain.interactor

import com.logger.UseCase
import com.logger.UseCaseScope
import com.logger.networklogger.data.RoomDbRepository
import com.logger.networklogger.domain.model.LogDataModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllLogUseCase(val roomDbRepository: RoomDbRepository) : UseCaseScope,
    UseCase<Flow<LogDataModel>> {
    override fun execute(): Flow<LogDataModel> {
        return flow {
            val result = roomDbRepository.getFullLog()
            emit(LogDataModel(result))
        }
    }

    override fun cancel() {
        coroutineContext.cancel()
    }
}