package com.example.planetracker.views.map

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetracker.apis.OpenSkyAPI
import com.example.planetracker.models.Plane
import com.example.planetracker.repos.OpenSkyRepo
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*
import kotlin.concurrent.timerTask

class MapViewModel : ViewModel() {

    var mMap: GoogleMap? = null

    private val repo: OpenSkyRepo = OpenSkyRepo()
    var _allPlanes = MutableLiveData<SnapshotStateList<Plane>>()
    val allPlanes: LiveData<SnapshotStateList<Plane>>
        get() = _allPlanes
    val planeImpl = mutableStateListOf<Plane>()

    val planesInRegion = MutableLiveData<List<Plane>>()

    // Bounds for northern europe
    val lamin = "53.9"
    val lamax = "70.1"
    val lomin = "0.0"
    val lomax = "35.0"

    private val refreshIntervalMillis = 5000






    fun getAllPlanes () {
        Timer().scheduleAtFixedRate(timerTask {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val serverResp = repo.getAllPlanes()
                    print("PLANES FOUND: " + serverResp.size)
                    planeImpl.clear()
                    planeImpl.addAll(serverResp)
                   // _allPlanes.postValue(null)
                    _allPlanes.postValue(planeImpl)
                } catch (e: Exception) {
                    print(e.stackTrace)
                }
            }
        }, refreshIntervalMillis.toLong(),refreshIntervalMillis.toLong())

    }

    fun getPlanesByBounds() {
        viewModelScope.launch(Dispatchers.IO) {
            val serverResp = repo.getPlanesByBounds(lamin = lamin, lomin = lomin, lomax = lomax, lamax = lamax)
            planesInRegion.postValue(serverResp)
        }
    }

}