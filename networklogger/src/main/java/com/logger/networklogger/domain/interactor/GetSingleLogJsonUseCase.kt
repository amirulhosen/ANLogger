package com.logger.networklogger.domain.interactor

import com.logger.UseCaseScope
import com.logger.UseCaseWithParameter
import com.logger.networklogger.data.RoomDbRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class GetSingleLogJsonUseCase(val roomDbRepository: RoomDbRepository) : UseCaseScope,
    UseCaseWithParameter<Long, Flow<File>> {
    override fun execute(id: Long): Flow<File> {
        return flow {
            val result = roomDbRepository.getRow(id)
            val json = roomDbRepository.getSingleLogJson(result)
            emit(json)
        }
    }

    override fun cancel() {
        coroutineContext.cancel()
    }
}