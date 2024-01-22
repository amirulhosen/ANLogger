package com.logger.networklogger.ui.list

import androidx.lifecycle.*
import com.logger.networklogger.domain.interactor.DeleteAllLogUseCase
import com.logger.networklogger.domain.interactor.GetAllLogUseCase
import com.logger.networklogger.domain.interactor.ShareLogUseCase
import com.logger.networklogger.domain.model.LogDataModel
import com.logger.observers.Event
import java.io.File

class LogListViewmodel(
    private val getAllLogUseCase: GetAllLogUseCase,
    private val deleteAllLogUseCase: DeleteAllLogUseCase,
    private val getAllLogJsonUseCase: ShareLogUseCase
) : ViewModel() {

    //Inputs
    internal val logListRequest = MutableLiveData<Unit>()
    internal val logGsonRequest = MutableLiveData<Int>()
    internal val deleteLogRequest = MutableLiveData<Unit>()

    //Outputs

    private val _logListResponse = MediatorLiveData<LogDataModel>()
    private val _logJsonResponse = MediatorLiveData<Event<File>>()
    private val _deleteLogResponse = MediatorLiveData<Event<Unit>>()

    val logListResponse: LiveData<LogDataModel> get() = _logListResponse
    val deleteLogResponse: LiveData<Event<Unit>> get() = _deleteLogResponse
    val logJsonResponse: LiveData<Event<File>> get() = _logJsonResponse

    // Transformation
    private val logResult: LiveData<LogDataModel> =
        logListRequest.switchMap {
            getAllLogUseCase.execute().asLiveData()
        }
    private val deleteLogResult: LiveData<Boolean> =
        deleteLogRequest.switchMap {
            deleteAllLogUseCase.execute().asLiveData()
        }

    private val logGsonResult: LiveData<File> =
        logGsonRequest.switchMap {
            getAllLogJsonUseCase.execute(it).asLiveData()
        }

    init {
        _logListResponse.addSource(logResult) {
            _logListResponse.postValue(it)
        }
        _deleteLogResponse.addSource(deleteLogResult) {
            _deleteLogResponse.postValue(Event(Unit))
        }
        _logJsonResponse.addSource(logGsonResult) {
            _logJsonResponse.postValue(Event(it))
        }
    }
}