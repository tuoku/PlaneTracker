package com.example.planetracker.views.map

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.planetracker.R
import com.example.planetracker.models.Plane
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GoogleMaps(model: MapViewModel) {
    val planes by model.allPlanes.observeAsState()
    val planesNorthEurope: List<Plane> by model.planesInRegion.observeAsState(emptyList())

   // model.getPlanesByBounds()

    var mapView = rememberMapViewWithLifeCycle()
    val markers: MutableList<MarkerOptions> = mutableListOf()
    val builder: LatLngBounds.Builder = LatLngBounds.Builder()
    builder.include(LatLng(69.197, 0.718))
    builder.include(LatLng(54.197, 3.892))
    builder.include(LatLng(70.833, 42.245))
    builder.include(LatLng(56.550, 38.426))
    val eu: LatLngBounds = builder.build()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    val builtMarkers: MutableList<Marker> = mutableListOf()


    val helsinki = LatLng(60.16345897617068, 24.930291611319266)
    var selectedMarker: Marker? by remember { mutableStateOf(null)}


    LaunchedEffect(true) {
        model.getAllPlanes()
    }

        planes?.forEach {

            if (it.latitude != null && it.longitude != null && model.mMap != null) {

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





    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetContent = {
            val plane = selectedMarker?.tag as Plane?
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
               Text("ICAO24: ${plane?.icao24 ?: "???" }")
                Text("Callsign: ${plane?.callsign ?: "???" }")
                Text("Origin: ${plane?.originCountry ?: "???" }")
            }
        }
    )  {
                AndroidView(
                    factory = { mapView },


                    ) { mapView: MapView ->
                    CoroutineScope(Dispatchers.Main).launch {

                        mapView.getMapAsync { map ->

                            model.mMap = map
                            updateMarkers(model, markers)
                            model.mMap!!.setOnMarkerClickListener {
                                selectedMarker = it
                                coroutineScope.launch {
                                    bottomState.show()
                                }
                                true
                            }


                        }

                    }
                }
            }


}

fun updateMarkers(model: MapViewModel, markers: List<MarkerOptions>) {
    val handler = Handler(Looper.getMainLooper())
    var x = 0
    val DELAY: Long = 1
    if(model.mMap != null) {
        model.mMap!!.clear()
        markers.forEach {

            handler.postDelayed(
                {
                    val marker = model.mMap!!.addMarker(it)
                    marker!!.tag = model.allPlanes.value?.find {plane -> plane.icao24 == it.title }

                }, DELAY * x++.toLong()
            )

        }

    }
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

