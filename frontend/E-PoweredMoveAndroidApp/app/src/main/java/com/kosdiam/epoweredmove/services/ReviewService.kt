package com.kosdiam.epoweredmove.services

import com.kosdiam.epoweredmove.BuildConfig
import com.kosdiam.epoweredmove.services.interfaces.IPlugService
import com.kosdiam.epoweredmove.services.interfaces.IPoiService
import com.kosdiam.epoweredmove.services.interfaces.IReviewService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewService(tokenId: String? = "") {
    val httpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request().newBuilder().addHeader("authorization", tokenId?:"").build()
        chain.proceed(request)
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://" + BuildConfig.BACK_END_DOMAIN + ":" + BuildConfig.BACK_END_PORT + "/epoweredmove/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient.build())
        .build()
    val reviewService: IReviewService = retrofit.create(IReviewService::class.java)
}