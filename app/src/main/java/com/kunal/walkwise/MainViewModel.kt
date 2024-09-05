package com.kunal.walkwise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val mainRepository = MainRepository.get()

    private var isNewUser = false

    private val _userStepsData: MutableStateFlow<Long> by lazy {
        MutableStateFlow(0L)
    }

    val userStepsData: StateFlow<Long> = _userStepsData.asStateFlow()

    init {
        viewModelScope.launch {
            isNewUser = mainRepository.isNewUser("2")
            if (isNewUser) {
                mainRepository.createNewUserEntry("2")
                isNewUser = false
            } else {
                println("not a new user")
            }

            mainRepository.getStepsFromUserId("2").collect { steps ->
                _userStepsData.value = steps
            }
        }
    }
}