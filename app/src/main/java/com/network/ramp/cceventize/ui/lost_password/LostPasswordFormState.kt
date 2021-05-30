package com.network.ramp.cceventize.ui.lost_password

/**
 * Data validation state of the lost password form.
 */
data class LostPasswordFormState (
    var emailError: Int? = null,
    val isDataValid: Boolean = false
)