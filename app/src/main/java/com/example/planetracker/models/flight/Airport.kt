package com.example.planetracker.models.flight

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class Airport {

    @SerializedName("icao")
    @Expose
    var icao: String? = null

    @SerializedName("iata")
    @Expose
    var iata: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("shortName")
    @Expose
    var shortName: String? = null

    @SerializedName("municipalityName")
    @Expose
    var municipalityName: String? = null

    @SerializedName("location")
    @Expose
    var location: Location? = null

    @SerializedName("countryCode")
    @Expose
    var countryCode: String? = null
}