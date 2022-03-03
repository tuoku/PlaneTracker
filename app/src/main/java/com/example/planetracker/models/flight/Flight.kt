package com.example.planetracker.models.flight

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class Flight {
    @SerializedName("greatCircleDistance")
    @Expose
    var greatCircleDistance: GreatCircleDistance? = null

    @SerializedName("departure")
    @Expose
    var departure: Departure? = null

    @SerializedName("arrival")
    @Expose
    var arrival: Arrival? = null

    @SerializedName("lastUpdatedUtc")
    @Expose
    var lastUpdatedUtc: String? = null

    @SerializedName("number")
    @Expose
    var number: String? = null

    @SerializedName("callSign")
    @Expose
    var callSign: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("codeshareStatus")
    @Expose
    var codeshareStatus: String? = null

    @SerializedName("isCargo")
    @Expose
    var isCargo: Boolean? = null

    @SerializedName("aircraft")
    @Expose
    var aircraft: Aircraft? = null

    @SerializedName("airline")
    @Expose
    var airline: Airline? = null

}