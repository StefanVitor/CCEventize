package com.network.ramp.cceventize.ui.event_crud

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.network.ramp.cceventize.R

/**
 * ViewModel class for event form
 */
class EventViewModel : ViewModel() {
    private val _eventForm = MutableLiveData<EventFormState>()
    val eventFormState: LiveData<EventFormState> = _eventForm

    fun eventDataChanged(eventName: String, eventDescription: String, eventTicketPrice: Double, eventCoinAddress: String,
        coinPriceEur: Double?, coinMinPurchaseAmountEur: Double?) {
        if (!isEventName(eventName)) {
            _eventForm.value = EventFormState(nameError = R.string.invalid_event_name)
        } else if (!isDescriptionValid(eventDescription)) {
            _eventForm.value = EventFormState(descriptionError = R.string.invalid_event_description)
        } else if (!isTicketPriceNotEmpty(eventTicketPrice)) {
            _eventForm.value = EventFormState(ticketPriceError = R.string.invalid_event_ticket_price_zero)
        } else if (!isTicketPriceValid(eventTicketPrice, coinPriceEur, coinMinPurchaseAmountEur)) {
            _eventForm.value = EventFormState(ticketPriceError = R.string.invalid_event_invalid_ticket_price)
        } else if (!isCoinAddressValid(eventCoinAddress)) {
            _eventForm.value = EventFormState(coinAddressError = R.string.invalid_event_description)
        } else {
            _eventForm.value = EventFormState(isDataValid = true)
        }
    }

    // Check is event name not empty
    private fun isEventName(eventName: String): Boolean {
        return eventName.isNotBlank()
    }

    // Check is event description not empty
    private fun isDescriptionValid(eventDescription: String): Boolean {
        return eventDescription.isNotBlank()
    }

    // Check is ticket price not empty
    private fun isTicketPriceNotEmpty(eventTicketPrice: Double): Boolean {
        if (eventTicketPrice == 0.0)
        {
            return false
        }
        return true
    }

    // Check is ticket price valid (ticket price is larger than min ticket price, so user can buy one ticket)
    // If this validation not exist, users will not be able to buy one ticket, because,
    // ticket price would be smaller than minimum coin puchase amount on RAMP network
    private fun isTicketPriceValid(eventTicketPrice: Double, coinPriceEur: Double?, coinMinPurchaseAmountEur: Double?): Boolean {
        val minTicketPrice = coinMinPurchaseAmountEur?.div(coinPriceEur!!)
        if (eventTicketPrice >= minTicketPrice!!)
        {
            return true
        }
        return false
    }

    // Check is coin address not empty
    private fun isCoinAddressValid(eventCoinAddress: String): Boolean {
        return eventCoinAddress.isNotBlank()
    }
}