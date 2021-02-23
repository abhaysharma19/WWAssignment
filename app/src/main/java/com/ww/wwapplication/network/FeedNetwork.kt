package com.ww.wwapplication.network

import com.ww.wwapplication.model.FeedResponse
import retrofit2.Call
import retrofit2.http.GET

const val  BASE_URL = "https://newsapi.org/v2/"
const val API_KEY = "182f0e52971d4ee58f09856bdbc9b44b"
interface FeedNetwork {
    @GET("top-headlines?country=in&apiKey=${API_KEY}")
    fun getFeeds(): Call<FeedResponse>
}