package com.example.planetracker.repos

import com.example.planetracker.apis.OpenSkyAPI
import com.example.planetracker.models.Plane
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import java.lang.Exception
import java.lang.Integer.parseInt

class OpenSkyRepo {

    private val call = OpenSkyAPI.service

    suspend fun getAllPlanes(): List<Plane> {
        val states = call.allPlanes().states ?: emptyList()
        return constructPlanes(states)
    }

    private suspend fun constructPlanes(states: List<List<Any>>): List<Plane> {
        var planes: MutableList<Plane> = mutableListOf<Plane>()

        states.forEach {
            try {
                planes.add(
                    Plane(
                        icao24 = it[0] as? String ?: "",
                        callsign = it[1] as? String?,
                        originCountry = it[2] as? String ?: "",
                        timePosition = it[3] as Double,
                        lastContact = it[4] as Double,
                        longitude = it[5] as Double,
                        latitude = it[6] as Double,
                        baroAltitude = it[7] as Double,
                        onGround = it[8] as? Boolean ?: true,
                        velocity = it[9] as Double,
                        trueTrack = it[10] as Double,
                        verticalRate = it[11] as Double,
                        sensors = it[12] as? List<Int>?,
                        geoAltitude = it[13] as Double,
                        squawk = it[14] as? String?,
                        SPI = it[15] as? Boolean ?: false,
                        positionSource = it[16] as Double
                    )
                )
            } catch(e: Exception) {
                print("${e.message} \n")
            }
        }
        return planes
    }

}