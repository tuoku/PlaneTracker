package com.example.planetracker.views.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.android.gms.maps.model.CameraPosition

import com.google.android.gms.maps.GoogleMapOptions




val singapore = LatLng(1.35, 103.87)

@Composable
fun MapView () {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        googleMapOptionsFactory = {
            GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(singapore, 10f))
        }
    )
}