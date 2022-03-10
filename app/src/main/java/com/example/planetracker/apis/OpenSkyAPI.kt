package com.example.planetracker.apis

import com.example.planetracker.models.Plane
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


object OpenSkyAPI {
    private const val URL = "https://opensky-network.org/api/"

    object Model {
        class Res {
            @SerializedName("time")
            @Expose
            var time: Int? = null

            @SerializedName("states")
            @Expose
            var states: List<List<Any>>? = null
        }
    }

    interface Service {
        @GET("states/all")
        suspend fun allPlanes(@Query("")p: String = ""): Model.Res

        @GET("states/all")
        suspend fun planesByBounds(
            @Query("lamin")lamin: String,
            @Query("lomin")lomin: String,
            @Query("lamax")lamax: String,
            @Query("lomax")lomax: String): Model.Res
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(Service::class.java)!!
}