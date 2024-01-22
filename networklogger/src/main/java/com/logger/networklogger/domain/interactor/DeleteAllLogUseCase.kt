package com.logger.networklogger.domain.interactor

import com.logger.UseCase
import com.logger.UseCaseScope
import com.logger.networklogger.data.RoomDbRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteAllLogUseCase(val roomDbRepository: RoomDbRepository) : UseCaseScope,
    UseCase<Flow<Boolean>> {
    override fun execute(): Flow<Boolean> {
        return flow {
            roomDbRepository.deleteTable()
            emit(true)
        }
    }

    override fun cancel() {
        coroutineContext.cancel()
    }
}