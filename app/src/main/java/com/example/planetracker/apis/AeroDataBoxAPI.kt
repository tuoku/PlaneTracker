package com.example.planetracker.apis

import android.content.Context
import android.content.res.Resources
import com.example.planetracker.R
import com.example.planetracker.models.PlaneInfo
import com.example.planetracker.models.flight.Flight
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


object AeroDataBoxAPI {

    @Volatile
    private lateinit var appContext: Context

    fun setContext(context: Context){
        appContext = context
    }

    private const val URL = "https://aerodatabox.p.rapidapi.com/"

    object Model {
        class Res {
            @SerializedName("url")
            @Expose
            var url: String? = null

            @SerializedName("webUrl")
            @Expose
            var webUrl: String? = null

            @SerializedName("author")
            @Expose
            var author: String? = null

            @SerializedName("title")
            @Expose
            var title: String? = null

            @SerializedName("description")
            @Expose
            var description: String? = null

            @SerializedName("license")
            @Expose
            var license: String? = null

            @SerializedName("htmlAttributions")
            @Expose
            var htmlAttributions: List<String>? = null

        }
    }


    interface Service {
        @Headers(
            "x-rapidapi-host: aerodatabox.p.rapidapi.com"
        )
        @GET("aircrafts/icao24/{icao}")
        suspend fun planeByIcao24(
            @Path("icao") icao: String,
            @Header("x-rapidapi-key") apiKey: String = appContext.getString(R.string.rapid_api_key)
        ): PlaneInfo

        @Headers(
            "x-rapidapi-host: aerodatabox.p.rapidapi.com",
        )
        @GET("aircrafts/reg/{reg}/image/beta")
        suspend fun planeImage(
            @Path("reg")registration: String,
            @Header("x-rapidapi-key") apiKey: String = appContext.getString(R.string.rapid_api_key)): Model.Res

        @Headers(
            "x-rapidapi-host: aerodatabox.p.rapidapi.com",
        )
        @GET("flights/icao24/{icao}")
        suspend fun flightStatus(
            @Path("icao")icao: String,
            @Query("withAircraftImage")withImg: Boolean = true,
            @Header("x-rapidapi-key") apiKey: String = appContext.getString(R.string.rapid_api_key)
        ): List<Flight>
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(Service::class.java)!!
}