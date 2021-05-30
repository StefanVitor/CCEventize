package com.network.ramp.cceventize.ui.event_crud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EventViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}