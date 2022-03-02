package com.example.planetracker.views.map

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetracker.apis.AeroDataBoxAPI
import com.example.planetracker.models.Plane
import com.example.planetracker.models.PlaneInfo
import com.example.planetracker.repos.AeroDataBoxRepo
import com.example.planetracker.repos.OpenSkyRepo
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.concurrent.timerTask

class MapViewModel : ViewModel() {

    var mMap: GoogleMap? = null

    private val openSkyRepo: OpenSkyRepo = OpenSkyRepo()
    private val aeroDataBoxRepo: AeroDataBoxRepo = AeroDataBoxRepo()
    var _allPlanes = MutableLiveData<SnapshotStateList<Plane>>()
    val allPlanes: LiveData<SnapshotStateList<Plane>>
        get() = _allPlanes
    val planeImpl = mutableStateListOf<Plane>()

    var _planeInfo = MutableLiveData<PlaneInfo>()
    val planeInfo: LiveData<PlaneInfo>
        get() = _planeInfo

    val planeInfoCache = mutableListOf<PlaneInfo>()

    var _planeImg = MutableLiveData<String>()
    val planeImg: LiveData<String>
        get() = _planeImg

    val planeImgCache = mutableListOf<AeroDataBoxAPI.Model.Res>()


    val planesInRegion = MutableLiveData<List<Plane>>()

    // Bounds for northern europe
    val lamin = "53.9"
    val lamax = "70.1"
    val lomin = "0.0"
    val lomax = "35.0"

    private val refreshIntervalMillis = 0

    fun getPlaneInfo(icao24: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val serverResp = aeroDataBoxRepo.getPlaneByIcao(icao24)
                planeInfoCache.add(serverResp)
                _planeInfo.postValue(serverResp)
            } catch (e: Exception) {
                print(e.stackTrace)
            }
        }
    }

    fun getPlaneImage(registration: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resp = aeroDataBoxRepo.getPlaneImage(registration)
                planeImgCache.add(resp)
                _planeImg.postValue(resp.url)
            } catch (e: Exception) {
                print(e.message)
                _planeImg.postValue("404")
            }
        }
    }

    fun getAllPlanes () {
        // set interval to 0 to only fetch once
        if(refreshIntervalMillis == 0) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val serverResp = openSkyRepo.getAllPlanes()
                    print("PLANES FOUND: " + serverResp.size)
                    planeImpl.clear()
                    planeImpl.addAll(serverResp)
                    // _allPlanes.postValue(null)
                    _allPlanes.postValue(planeImpl)
                } catch (e: Exception) {
                    print(e.stackTrace)
                }
            }
        } else {
            Timer().scheduleAtFixedRate(timerTask {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val serverResp = openSkyRepo.getAllPlanes()
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


    }

    fun getPlanesByBounds() {
        viewModelScope.launch(Dispatchers.IO) {
            val serverResp = openSkyRepo.getPlanesByBounds(lamin = lamin, lomin = lomin, lomax = lomax, lamax = lamax)
            planesInRegion.postValue(serverResp)
        }
    }

}