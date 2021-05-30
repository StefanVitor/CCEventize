package com.network.ramp.cceventize.ui.event_crud

/**
 * Data validation state of the event form.
 */
data class EventFormState (
    val nameError: Int? = null,
    val descriptionError: Int? = null,
    val ticketPriceError: Int? = null,
    val coinAddressError: Int? = null,
    val isDataValid: Boolean = false
)