package com.kosdiam.epoweredmove.services

import com.kosdiam.epoweredmove.BuildConfig
import com.kosdiam.epoweredmove.services.interfaces.IPoiService
import com.kosdiam.epoweredmove.services.interfaces.IReservationService
import com.kosdiam.epoweredmove.services.interfaces.IUserService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserService(tokenId: String? = "") {
    val httpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request().newBuilder().addHeader("authorization", tokenId?:"").build()
        chain.proceed(request)
    }

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://" + BuildConfig.BACK_END_DOMAIN + ":" + BuildConfig.BACK_END_PORT + "/epoweredmove/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient.build())
        .build()
    val userService: IUserService = retrofit.create(IUserService::class.java)
}