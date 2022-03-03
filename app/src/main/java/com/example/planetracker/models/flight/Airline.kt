package com.example.planetracker.models.flight

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class Airline {
    @SerializedName("name")
    @Expose
    var name: String? = null
}