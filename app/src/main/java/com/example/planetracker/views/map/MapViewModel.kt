package com.example.planetracker.views.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetracker.apis.OpenSkyAPI
import com.example.planetracker.models.Plane
import com.example.planetracker.repos.OpenSkyRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val repo: OpenSkyRepo = OpenSkyRepo()
    val allPlanes = MutableLiveData<List<Plane>>()
    fun getAllPlanes () {
        viewModelScope.launch(Dispatchers.IO) {
            val serverResp = repo.getAllPlanes()
            print("PLANES FOUND: " + serverResp.size)
            allPlanes.postValue(serverResp)
        }
    }

}