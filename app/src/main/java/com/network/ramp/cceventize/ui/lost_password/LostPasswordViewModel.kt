package com.network.ramp.cceventize.ui.lost_password

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.network.ramp.cceventize.R

class LostPasswordViewModel : ViewModel() {
    private val _lostPasswordForm = MutableLiveData<LostPasswordFormState>()
    val lostPasswordFormState: LiveData<LostPasswordFormState> = _lostPasswordForm

    fun lostPasswordDataChanged(email: String) {
        if (!isEmailValid(email)) {
            _lostPasswordForm.value = LostPasswordFormState(emailError = R.string.invalid_email)
        }
        else {
            _lostPasswordForm.value = LostPasswordFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isEmailValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

}