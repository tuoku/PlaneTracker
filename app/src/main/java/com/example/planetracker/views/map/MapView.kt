package com.example.planetracker.views.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.planetracker.R
import com.example.planetracker.apis.AeroDataBoxAPI
import com.example.planetracker.models.Plane
import com.example.planetracker.models.PlaneInfo
import com.example.planetracker.repos.AeroDataBoxRepo
import com.example.planetracker.views.favs.FavsViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import coil.compose.rememberImagePainter


@OptIn(ExperimentalMaterialApi::class, coil.annotation.ExperimentalCoilApi::class)
@Composable
fun GoogleMaps(model: MapViewModel, favsViewModel: FavsViewModel) {
    val planes by model.allPlanes.observeAsState()
    val planeInfo: PlaneInfo? by model.planeInfo.observeAsState(null)
    val planeImg: String? by model.planeImg.observeAsState(null)
    val planesNorthEurope: List<Plane> by model.planesInRegion.observeAsState(emptyList())
    val favorites = favsViewModel.getFavorites().observeAsState(listOf())
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
    var selectedMarker: Marker? by remember { mutableStateOf(null) }
    val planePainter = if (planeImg == null){
       painterResource(id = R.drawable.plane_placeholder)
    } else {
        rememberImagePainter(planeImg)
    }


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
        sheetShape = RoundedCornerShape(32.dp),
        sheetState = bottomState,
        sheetContent = {

            val plane = selectedMarker?.tag as Plane?

            if (plane != null) {
                if ((model.planeInfoCache.firstOrNull { it.hexIcao?.lowercase() == plane.icao24.lowercase() }) == null) {
                    model.getPlaneInfo(plane.icao24).also {
                        planeInfo?.reg?.let { it1 -> model.getPlaneImage(it1) }
                    }

                }

            }
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize()

            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)

                ){

                        (if(planeImg != null) {
                            if(planeImg == "404") {
                                Text("No image found", modifier = Modifier.align(Alignment.Center))
                            } else {
                                Image(painter = planePainter,
                                    alignment = Alignment.TopCenter,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize())
                            }

                        } else {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        })


                        Column(
                            Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black),
                                        0f,
                                        250f,
                                    )
                                )
                                .align(Alignment.BottomCenter)
                        ) {


                        }
                    Text(planeInfo?.typeName ?: "", modifier = Modifier.align(Alignment.BottomStart), color = Color.White, fontSize = 32.sp)



                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)) {
                        IconButton(onClick = {
                            if(plane != null) {
                                if((favorites.value.firstOrNull { it.icao24 == plane.icao24 }) == null) {
                                    favsViewModel.addFavorite(plane)
                                } else {
                                    favsViewModel.removeFavoriteByIcao(plane.icao24)
                                }

                            }
                        }) {
                            var icon =
                                if ((favorites.value.firstOrNull { it.icao24 == plane?.icao24 ?: "VERY CONFUSING ICAO STRING" }) != null) {
                                    Icons.Filled.Star
                                }   else Icons.Outlined.StarOutline
                            Icon(imageVector = icon,
                                contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(40.dp))

                        }
                    }


                }

                Text("ICAO24: ${plane?.icao24 ?: "???"}")
                Text("Callsign: ${plane?.callsign ?: "???"}")
                Text("Origin: ${plane?.originCountry ?: "???"}")
                Text("Type: ${planeInfo?.typeName ?: "???"}")
                Text("Registration: ${planeInfo?.reg ?: "???"}")
                Text("Operated by: ${planeInfo?.airlineName ?: "???"}")

            }
        }
    ) {
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
    val DELAY: Long = 3
    if (model.mMap != null) {
        model.mMap!!.clear()
        markers.forEach {

            handler.postDelayed(
                {
                    val marker = model.mMap!!.addMarker(it)
                    marker!!.tag = model.allPlanes.value?.find { plane -> plane.icao24 == it.title }

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
            when (event) {
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

