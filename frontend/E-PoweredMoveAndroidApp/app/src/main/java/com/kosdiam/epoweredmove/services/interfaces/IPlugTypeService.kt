package com.kosdiam.epoweredmove.services.interfaces

import com.kosdiam.epoweredmove.models.PlugType
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IPlugTypeService {

    @GET("plugType/all")
    fun getPlugTypes(): Call<List<PlugType>>
    @GET("plugType/allByChargingStation")
    fun getPlugTypesByChargingStation(@Query("chargingStationId") chargingStationId: String): Call<List<PlugType>>
}