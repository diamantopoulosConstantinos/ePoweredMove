package com.kosdiam.epoweredmove.services.interfaces

import com.kosdiam.epoweredmove.models.Poi
import com.kosdiam.epoweredmove.models.Review
import com.kosdiam.epoweredmove.models.User
import retrofit2.Call
import retrofit2.http.*

interface IReviewService {
    @GET("review/allByChargingStation")
    fun getReviewsByChargingStation(@Query("chargingStationId") chargingStationId: String): Call<List<Review>>

    @POST("review")
    fun createReview(@Body reviewRequest: Review): Call<Review>
}