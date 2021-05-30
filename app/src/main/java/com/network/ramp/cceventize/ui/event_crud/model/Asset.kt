package com.network.ramp.cceventize.ui.event_crud.model

/**
 * Data class with information about asset from RAMP API
 */
data class Asset(
    val symbol: String,
    val name: String,
    val minPurchaseAmountEur: Double,
    val priceEur: Double,
    val decimals: Int
)
