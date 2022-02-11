package com.example.planetracker.views.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetracker.apis.OpenSkyAPI
import com.example.planetracker.models.Plane
import com.example.planetracker.repos.OpenSkyRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MapViewModel : ViewModel() {

    private val repo: OpenSkyRepo = OpenSkyRepo()
    val allPlanes = MutableLiveData<List<Plane>>()
    val planesInRegion = MutableLiveData<List<Plane>>()

    // Bounds for northern europe
    val lamin = "53.9"
    val lamax = "70.1"
    val lomin = "0.0"
    val lomax = "35.0"

    fun getAllPlanes () {
        viewModelScope.launch {
            try {
                val serverResp = withContext(Dispatchers.IO) { repo.getAllPlanes() }
                print("PLANES FOUND: " + serverResp.size)
                allPlanes.postValue(serverResp)
            } catch (e: Exception) {
                print(e.stackTrace)
            }
        }
    }

    fun getPlanesByBounds() {
        viewModelScope.launch(Dispatchers.IO) {
            val serverResp = repo.getPlanesByBounds(lamin = lamin, lomin = lomin, lomax = lomax, lamax = lamax)
            planesInRegion.postValue(serverResp)
        }
    }

}