package com.logger

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.logger.networklogger.domain.model.LogDataModel

class LogNavigationViewModel: ViewModel() {
    internal var log = MutableLiveData<LogDataModel.Log>()
    internal var logPosition = MutableLiveData<Long>()
}