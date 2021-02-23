package com.ww.wwapplication.repository

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.os.BuildCompat
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.ww.wwapplication.model.Feed
import com.ww.wwapplication.model.FeedResponse
import com.ww.wwapplication.network.BASE_URL
import com.ww.wwapplication.network.FeedNetwork
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FeedListRepository(val application: Application) {
    val showLoader = MutableLiveData<Boolean>()

    val feedsList = MutableLiveData<List<Feed>>()
    val selectedFeed = MutableLiveData<Feed>()

    fun changeState() {
        showLoader.value = !(showLoader.value != null && showLoader.value!!)
    }

    fun fetchFeeds() {
        showLoader.value = true
        // Networkcall

        val retrofit =
            Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient).addConverterFactory(
                GsonConverterFactory.create()
            )
                .build()


        val service = retrofit.create(FeedNetwork::class.java)

        service.getFeeds().enqueue(object : Callback<FeedResponse> {
            override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                showLoader.value = false
                Toast.makeText(application, "Error wile accessing the API", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(
                call: Call<FeedResponse>,
                response: Response<FeedResponse>
            ) {
                Log.d("FeedListRepository", "Response : ${Gson().toJson(response.body())}")
                feedsList.value = response.body()?.articles
                showLoader.value = false
            }

        })
    }


    fun hasNetwork(): Boolean? {
        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            return networkCapabilities != null &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }else {
            var isConnected: Boolean? = false // Initial Value
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
            return isConnected
        }
    }

    val cacheSize = (5 * 1024 * 1024).toLong()
    val myCache = Cache(application.cacheDir, cacheSize)

    val okHttpClient = OkHttpClient.Builder()
        .cache(myCache)
        .addNetworkInterceptor { chain ->
            val originalResponse = chain.proceed(chain.request())
            val cacheControl = originalResponse.header("Cache-Control")
            if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains(
                    "no-cache"
                ) ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")
            ) {
                originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + 10)
                    .build()
            } else {
                originalResponse
            }
        }.addInterceptor {chain ->
            var request = chain.request()
            if(!hasNetwork()!!) {
                request = request.newBuilder().header(
                    "Cache-Control",
                    "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                ).removeHeader("Pragma").build()
            }
            chain.proceed(request)
        }.build()
}