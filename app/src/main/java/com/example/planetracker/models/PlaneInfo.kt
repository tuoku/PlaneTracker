package com.example.planetracker.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlaneInfo {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("reg")
    @Expose
    var reg: String? = null

    @SerializedName("active")
    @Expose
    var active: Boolean? = null

    @SerializedName("serial")
    @Expose
    var serial: String? = null

    @SerializedName("hexIcao")
    @Expose
    var hexIcao: String? = null

    @SerializedName("airlineId")
    @Expose
    var airlineId: String? = null

    @SerializedName("airlineName")
    @Expose
    var airlineName: String? = null

    @SerializedName("iataCodeShort")
    @Expose
    var iataCodeShort: String? = null

    @SerializedName("iataCodeLong")
    @Expose
    var iataCodeLong: String? = null

    @SerializedName("model")
    @Expose
    var model: String? = null

    @SerializedName("modelCode")
    @Expose
    var modelCode: String? = null

    @SerializedName("numSeats")
    @Expose
    var numSeats: Int? = null

    @SerializedName("rolloutDate")
    @Expose
    var rolloutDate: String? = null

    @SerializedName("firstFlightDate")
    @Expose
    var firstFlightDate: String? = null

    @SerializedName("deliveryDate")
    @Expose
    var deliveryDate: String? = null

    @SerializedName("registrationDate")
    @Expose
    var registrationDate: String? = null

    @SerializedName("typeName")
    @Expose
    var typeName: String? = null

    @SerializedName("numEngines")
    @Expose
    var numEngines: Int? = null

    @SerializedName("engineType")
    @Expose
    var engineType: String? = null

    @SerializedName("isFreighter")
    @Expose
    var isFreighter: Boolean? = null

    @SerializedName("productionLine")
    @Expose
    var productionLine: String? = null

    @SerializedName("ageYears")
    @Expose
    var ageYears: Double? = null

    @SerializedName("verified")
    @Expose
    var verified: Boolean? = null
}