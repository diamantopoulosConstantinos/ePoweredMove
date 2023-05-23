package com.kosdiam.epoweredmove.services.interfaces

import com.kosdiam.epoweredmove.models.Plug
import com.kosdiam.epoweredmove.models.Reservation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IPlugService {

    @GET("plug/allByChargingStation")
    fun getPlugsByChargingStation(@Query("chargingStationId") chargingStationId: String): Call<List<Plug>>
}