package com.networklogger

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LogNavigationViewModel: ViewModel() {
    internal var log = MutableLiveData<LogDataModel.Log>()
    internal var logPosition = MutableLiveData<Int>()
}