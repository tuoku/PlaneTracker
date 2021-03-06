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
import com.example.planetracker.models.flight.Flight
import com.example.planetracker.repos.AeroDataBoxRepo
import com.example.planetracker.repos.OpenSkyRepo
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
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

    var _flight = MutableLiveData<Flight>()
    val flight: LiveData<Flight>
        get() = _flight

    val flightCache = mutableListOf<Flight>()


    val planesInRegion = MutableLiveData<List<Plane>>()

    val markerOptions: MutableList<MarkerOptions> = mutableListOf()
    val builtMarkers: MutableList<Marker> = mutableListOf()


    // Bounds for northern europe
    val lamin = "57.3000"
    val lamax = "70.1000"
    val lomin = "5.2000"
    val lomax = "32.0000"

    private val refreshIntervalMillis = 0

    fun invalidateInfos() {
        _flight.postValue(null)
    }

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

    fun getFlightStatus(icao24: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resp = aeroDataBoxRepo.getFlightStatus(icao24)
                flightCache.addAll(resp)
                _flight.postValue(resp[0])
            } catch (e: Exception) {
                print(e.message)
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
        if (refreshIntervalMillis == 0) {
            viewModelScope.launch(Dispatchers.IO) {
                val serverResp = openSkyRepo.getPlanesByBounds(
                    lamin = lamin,
                    lomin = lomin,
                    lomax = lomax,
                    lamax = lamax
                )
                planeImpl.clear()
                planeImpl.addAll(serverResp)
                // _allPlanes.postValue(null)
                _allPlanes.postValue(planeImpl)
            }
        } else {
            Timer().scheduleAtFixedRate(timerTask {
                viewModelScope.launch(Dispatchers.IO) {
                    val serverResp = openSkyRepo.getPlanesByBounds(
                        lamin = lamin,
                        lomin = lomin,
                        lomax = lomax,
                        lamax = lamax
                    )
                    planeImpl.clear()
                    planeImpl.addAll(serverResp)
                    // _allPlanes.postValue(null)
                    _allPlanes.postValue(planeImpl)
                }
            }, refreshIntervalMillis.toLong(),refreshIntervalMillis.toLong())
        }
    }

}