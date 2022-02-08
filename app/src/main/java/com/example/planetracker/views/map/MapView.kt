package com.example.planetracker.views.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.example.planetracker.R
import com.example.planetracker.models.Plane
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.android.gms.maps.model.CameraPosition

import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.Marker


val singapore = LatLng(1.35, 103.87)

@Composable
fun MapView(model: MapViewModel) {
    val planes: List<Plane> by model.allPlanes.observeAsState(emptyList())
    model.getAllPlanes()
    GoogleMap(

        modifier = Modifier.fillMaxSize(),
        googleMapOptionsFactory = {
            GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(singapore, 10f))
                .rotateGesturesEnabled(false)
        }
    ) {
        Marker(position = singapore)
        planes.forEach {
            if (it.latitude != null && it.longitude != null) {
                Marker(
                    position = LatLng(it.latitude, it.longitude),
                    title = it.icao24,
                    icon = BitmapDescriptorFactory.fromResource(
                        R.drawable.aeroplane
                    ),
                    rotation = it.trueTrack?.toFloat() ?: 0f
                )
            }
        }
    }
}