package com.example.retrofitsample.data

import com.example.retrofitsample.data.model.RemoteResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService{
    @GET("images/search?limit=10")
    suspend fun listImages(
        @Query("api_key") apiKey: String
    ): RemoteResult
}

object RetrofitServiceFactory{
    fun makeRetrofitService(): RetrofitService{
        return Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RetrofitService::class.java)
    }
}