package com.example.planetracker.models.flight

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class Aircraft {
    @SerializedName("reg")
    @Expose
    var reg: String? = null

    @SerializedName("modeS")
    @Expose
    var modeS: String? = null

    @SerializedName("model")
    @Expose
    var model: String? = null

    @SerializedName("image")
    @Expose
    var image: Image? = null
}