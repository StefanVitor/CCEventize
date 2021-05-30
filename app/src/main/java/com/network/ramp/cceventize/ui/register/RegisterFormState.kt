package com.network.ramp.cceventize.ui.register

/**
 * Data validation state of the register form.
 */
data class RegisterFormState (
    val firstNameError: Int? = null,
    val lastNameError: Int? = null,
    var emailError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)