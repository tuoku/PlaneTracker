package com.example.planetracker.models.flight

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class Arrival {
    @SerializedName("airport")
    @Expose
    var airport: Airport? = null

    @SerializedName("scheduledTimeLocal")
    @Expose
    var scheduledTimeLocal: String? = null

    @SerializedName("actualTimeLocal")
    @Expose
    var actualTimeLocal: String? = null

    @SerializedName("runwayTimeLocal")
    @Expose
    var runwayTimeLocal: String? = null

    @SerializedName("scheduledTimeUtc")
    @Expose
    var scheduledTimeUtc: String? = null

    @SerializedName("actualTimeUtc")
    @Expose
    var actualTimeUtc: String? = null

    @SerializedName("runwayTimeUtc")
    @Expose
    var runwayTimeUtc: String? = null

    @SerializedName("terminal")
    @Expose
    var terminal: String? = null

    @SerializedName("baggageBelt")
    @Expose
    var baggageBelt: String? = null

    @SerializedName("quality")
    @Expose
    var quality: List<String>? = null
}