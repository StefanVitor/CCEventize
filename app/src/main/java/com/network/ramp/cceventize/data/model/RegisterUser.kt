package com.network.ramp.cceventize.data.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data class that captures information about user
 */
@IgnoreExtraProperties
data class RegisterUser(
    var first_name: String? = "",
    var last_name: String? = "",
    var email: String? = ""
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "first_name" to first_name,
            "last_name" to last_name,
            "email" to email
        )
    }
}