package com.network.ramp.cceventize.data.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data class that captures information about event
 */
@IgnoreExtraProperties
data class Event(
    //event type
    var type: String? = "",
    //event name (title)
    var name: String? = "",
    //event description
    var description: String? = "",
    //event start time
    var start_time: Long? = 0,
    //event end time
    var end_time: Long? = 0,
    //event crypto currency for pay
    var currency: String? = "",
    //price in crypto currency
    var price: Double? = 0.0,
    //crypto currency address
    var address: String? = "",
    //created user
    var user: String? = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "type" to type,
            "name" to name,
            "description" to description,
            "start_time" to start_time,
            "end_time" to end_time,
            "currency" to currency,
            "price" to price,
            "address" to address,
            "user" to user
        )
    }
}