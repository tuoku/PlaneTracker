package com.example.planetracker.models.flight

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class Image {
    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("webUrl")
    @Expose
    var webUrl: String? = null

    @SerializedName("author")
    @Expose
    var author: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("license")
    @Expose
    var license: String? = null

    @SerializedName("htmlAttributions")
    @Expose
    var htmlAttributions: List<String>? = null
}