package com.kosdiam.epoweredmove.services.interfaces

import com.kosdiam.epoweredmove.models.Poi
import com.kosdiam.epoweredmove.models.Review
import com.kosdiam.epoweredmove.models.RouteInfo
import com.kosdiam.epoweredmove.models.User
import com.kosdiam.epoweredmove.models.Vehicle
import retrofit2.Call
import retrofit2.http.*

interface IVehicleService {
    @GET("vehicle")
    fun getVehicle(@Query("id") id: String): Call<Vehicle>

    @GET("vehicle/allByUser")
    fun getVehiclesByUser(@Query("userId") userId: String): Call<List<Vehicle>>

    @GET("vehicle/routeInfoByVehicle")
    fun getRouteInfoByVehicle(@Query("vehicleId") vehicleId: String,
                              @Query("batteryPercentageRemaining") batteryPercentageRemaining: Int,
                              @Query("currentLatitude") currentLatitude: Float,
                              @Query("currentLongitude") currentLongitude: Float,
                              @Query("destinationLatitude") destinationLatitude: Float,
                              @Query("destinationLongitude") destinationLongitude: Float): Call<RouteInfo>

    @PUT("vehicle")
    fun updateVehicle(@Body vehicleRequest: Vehicle): Call<Vehicle>
    @POST("vehicle")
    fun createVehicle(@Body vehicleRequest: Vehicle): Call<Vehicle>

    @DELETE("vehicle")
    fun deleteVehicle(@Query("id") id: String): Call<Boolean>
}