package com.network.ramp.cceventize.ui.lost_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LostPasswordViewModelFactory  : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LostPasswordViewModel::class.java)) {
            return LostPasswordViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}