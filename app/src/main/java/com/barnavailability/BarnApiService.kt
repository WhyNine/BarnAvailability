package com.barnavailability

import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


private const val BASE_URL = "https://www.google.co.uk/"                // this address is not used
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

object BarnApi {
    val retrofitService: BarnApiService by lazy {
        retrofit.create(BarnApiService::class.java)}
}

interface BarnApiService {
    @GET
    suspend fun getPage(@Url url: String): String
}

