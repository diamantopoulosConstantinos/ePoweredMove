package com.kosdiam.epoweredmove.services.interfaces

import com.kosdiam.epoweredmove.models.Poi
import com.kosdiam.epoweredmove.models.Reservation
import com.kosdiam.epoweredmove.models.RouteInfo
import retrofit2.Call
import retrofit2.http.*

interface IReservationService {
    @GET("reservation/allByChargingStationOwner")
    fun getReservationsByChargingStationOwner(@Query("userId") userId: String): Call<List<Reservation>>

    @GET("reservation/allByOwner")
    fun getReservationsByOwner(@Query("userId") userId: String): Call<List<Reservation>>

    @GET("reservation/routeInfoByReservation")
    fun getRouteInfoByReservation(@Query("reservationId") reservationId: String,
                                  @Query("batteryPercentageRemaining") batteryPercentageRemaining: Int,
                                  @Query("currentLatitude") currentLatitude: Float,
                                  @Query("currentLongitude") currentLongitude: Float): Call<RouteInfo>

    @POST("reservation")
    fun createReservation(@Body reservationRequest: Reservation): Call<Reservation>

    @PUT("reservation")
    fun updateReservation(@Body reservationRequest: Reservation): Call<Reservation>
}