package com.example.planetracker.models.flight

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class Departure {
    @SerializedName("airport")
    @Expose
    var airport: Airport? = null

    @SerializedName("scheduledTimeLocal")
    @Expose
    var scheduledTimeLocal: String? = null

    @SerializedName("scheduledTimeUtc")
    @Expose
    var scheduledTimeUtc: String? = null

    @SerializedName("terminal")
    @Expose
    var terminal: String? = null

    @SerializedName("quality")
    @Expose
    var quality: List<String>? = null

    @SerializedName("actualTimeLocal")
    @Expose
    var actualTimeLocal: String? = null

    @SerializedName("runwayTimeLocal")
    @Expose
    var runwayTimeLocal: String? = null

    @SerializedName("actualTimeUtc")
    @Expose
    var actualTimeUtc: String? = null

    @SerializedName("runwayTimeUtc")
    @Expose
    var runwayTimeUtc: String? = null

    @SerializedName("checkInDesk")
    @Expose
    var checkInDesk: String? = null

    @SerializedName("gate")
    @Expose
    var gate: String? = null

    @SerializedName("runway")
    @Expose
    var runway: String? = null
}