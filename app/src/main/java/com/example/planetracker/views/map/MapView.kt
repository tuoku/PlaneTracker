package com.example.planetracker.views.map

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.planetracker.R
import com.example.planetracker.models.Plane
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.*


@Composable
fun GoogleMaps(model: MapViewModel) {
    val planes: List<Plane> by model.allPlanes.observeAsState(emptyList())
    val planesNorthEurope: List<Plane> by model.planesInRegion.observeAsState(emptyList())
     model.getAllPlanes()
   // model.getPlanesByBounds()

    var mapView = rememberMapViewWithLifeCycle()
    var mMap: GoogleMap? by remember { mutableStateOf(null) }
    val markers: MutableList<MarkerOptions> = mutableListOf<MarkerOptions>()
    val builder: LatLngBounds.Builder = LatLngBounds.Builder()
    builder.include(LatLng(69.197, 0.718))
    builder.include(LatLng(54.197, 3.892))
    builder.include(LatLng(70.833, 42.245))
    builder.include(LatLng(56.550, 38.426))
    val eu: LatLngBounds = builder.build()

    val helsinki = LatLng(60.16345897617068, 24.930291611319266)


        planes.forEach {

            if (it.latitude != null && it.longitude != null && mMap != null) {

                    markers.add(
                        MarkerOptions()
                            .title(it.icao24)
                            .position(LatLng(it.latitude, it.longitude))
                            .icon(
                                BitmapDescriptorFactory.fromResource(R.drawable.aeroplane)
                            )
                            .flat(true)
                            .rotation(it.trueTrack?.toFloat() ?: 0f)
                    )
                }
                

        }

        if(mMap != null) {
            markers.forEach {
                mMap!!.addMarker(it)
            }

        }




    Box(){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AndroidView(
                factory = {mapView},


            ) { mapView: MapView ->
                CoroutineScope(Dispatchers.Main).launch {

                    mapView.getMapAsync { map ->

                        mMap = map


                    }

                }
            }
        }

    }

}

private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}



@Composable
fun rememberMapViewWithLifeCycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = com.google.maps.android.ktx.R.id.map_frame
        }
    }
    val lifeCycleObserver = rememberMapLifecycleObserver(mapView)
    val lifeCycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifeCycle) {
        lifeCycle.addObserver(lifeCycleObserver)
        onDispose {
            lifeCycle.removeObserver(lifeCycleObserver)
        }
    }

    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }

