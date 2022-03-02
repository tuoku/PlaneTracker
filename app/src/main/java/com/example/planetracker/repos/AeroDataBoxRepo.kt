package com.example.planetracker.repos

import com.example.planetracker.apis.AeroDataBoxAPI
import com.example.planetracker.models.PlaneInfo

class AeroDataBoxRepo {
    private val call = AeroDataBoxAPI.service

    suspend fun getPlaneByIcao(icao24: String): PlaneInfo {
        return call.planeByIcao24(icao24)
    }

    suspend fun getPlaneImage(registration: String): AeroDataBoxAPI.Model.Res {
        return call.planeImage(registration)
    }

}