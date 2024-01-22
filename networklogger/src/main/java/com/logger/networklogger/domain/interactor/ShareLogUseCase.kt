package com.logger.networklogger.domain.interactor

import com.logger.UseCaseScope
import com.logger.UseCaseWithParameter
import com.logger.networklogger.data.RoomDbRepository
import com.logger.networklogger.domain.model.ShareType
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class ShareLogUseCase(val roomDbRepository: RoomDbRepository) : UseCaseScope,
    UseCaseWithParameter<Int, Flow<File>> {
    override fun execute(shareType:Int): Flow<File> {
        return flow {
            if(shareType == ShareType.SQLITE.type) {
                val result = roomDbRepository.exportToSQLite()
                emit(result)
            }else{
                val result = roomDbRepository.getFullLogJson()
                emit(result)
            }
        }
    }

    override fun cancel() {
        coroutineContext.cancel()
    }
}