package com.logger.networklogger.ui.details

import androidx.lifecycle.*
import com.logger.networklogger.domain.interactor.GetSingleLogJsonUseCase
import com.logger.observers.Event
import java.io.File

class LogDetailsViewmodel(
    private val getSingleLogJsonUseCase: GetSingleLogJsonUseCase
) : ViewModel() {

    //Inputs
    internal val logDetailsRequest = MutableLiveData<Long>()

    //Outputs

    private val _logDetailsResponse = MediatorLiveData<Event<File>>()

    val logDetailsResponse: LiveData<Event<File>> get() = _logDetailsResponse

    // Transformation
    private val logResult: LiveData<File> =
        logDetailsRequest.switchMap {
            getSingleLogJsonUseCase.execute(it).asLiveData()
        }

    init {
        _logDetailsResponse.addSource(logResult) {
            _logDetailsResponse.postValue(Event(it))
        }
    }
}