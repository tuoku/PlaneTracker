package com.example.planetracker.models.flight

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class GreatCircleDistance {
    @SerializedName("meter")
    @Expose
    var meter: Double? = null

    @SerializedName("km")
    @Expose
    var km: Double? = null

    @SerializedName("mile")
    @Expose
    var mile: Double? = null

    @SerializedName("nm")
    @Expose
    var nm: Double? = null

    @SerializedName("feet")
    @Expose
    var feet: Double? = null
}