package com.example.planetracker.models.flight

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class Location {

    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lon")
    @Expose
    var lon: Double? = null
}