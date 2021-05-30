package com.network.ramp.cceventize.ui.event_crud.model

import retrofit2.Call
import retrofit2.http.GET

/**
 * Asset service for RAMP API
 */
interface AssetService {
    @GET("/api/host-api/assets")
    fun getAssets(): Call<AssetsResponse>
}