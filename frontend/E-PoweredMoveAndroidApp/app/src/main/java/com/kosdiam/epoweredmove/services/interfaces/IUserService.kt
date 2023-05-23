package com.kosdiam.epoweredmove.services.interfaces

import androidx.annotation.Keep
import com.kosdiam.epoweredmove.models.Poi
import com.kosdiam.epoweredmove.models.User
import retrofit2.Call
import retrofit2.http.*

interface IUserService {

    @GET("user")
    fun getUser(@Query("id") id: String): Call<User>
    @POST("user/googleSignIn")
    fun googleSignIn(@Body userRequest: User): Call<Boolean>

    @POST("user/createUser")
    fun createUserLocally(@Body userRequest: User): Call<User>
}