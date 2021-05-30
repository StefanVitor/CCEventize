package com.network.ramp.cceventize.data.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data class that captures information about ticket
 */
@IgnoreExtraProperties
data class Ticket(
    var event: String? = "",
    var user: String? = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "event" to event,
            "user" to user
        )
    }
}