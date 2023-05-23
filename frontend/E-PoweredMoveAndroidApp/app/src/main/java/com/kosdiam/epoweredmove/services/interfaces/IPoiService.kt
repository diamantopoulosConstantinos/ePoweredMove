package com.kosdiam.epoweredmove.services.interfaces

import com.kosdiam.epoweredmove.models.Poi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IPoiService {
    @GET("poi/all")
    fun getPois(): Call<List<Poi>>

    @GET("poi/allWithPlugAvailability")
    fun getPoisWithPlugAvailability(@Query("vehicleId") vehicleId: String?): Call<List<Poi>>

    @GET("poi/searchPoiByLocation")
    fun getPOIByLocation(@Query("latitude") latitude: Double, @Query("longitude") longitude: Double, @Query("vehicleId") vehicleId: String): Call<Poi>
}