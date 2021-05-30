package com.network.ramp.cceventize.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.network.ramp.cceventize.R

class RegisterViewModel : ViewModel() {
    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    fun registerDataChanged(firstName: String, lastName: String,
                            email: String, password: String) {
        if (!isFirstNameValid(firstName)) {
            _registerForm.value = RegisterFormState(firstNameError = R.string.invalid_first_name)
        }
        else if (!isLastNameValid(lastName)) {
            _registerForm.value = RegisterFormState(lastNameError = R.string.invalid_last_name)
        }
        else if (!isEmailValid(email)) {
            _registerForm.value = RegisterFormState(emailError = R.string.invalid_email)
        }
        else if (!isPasswordValid(password)) {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        }
        else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }

    //check is first name not empty
    private fun isFirstNameValid(firstName: String): Boolean {
        return firstName.isNotBlank();
    }

    //check is last name not empty
    private fun isLastNameValid(lastName: String): Boolean {
        return lastName.isNotBlank();
    }

    // A placeholder username validation check
    private fun isEmailValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}