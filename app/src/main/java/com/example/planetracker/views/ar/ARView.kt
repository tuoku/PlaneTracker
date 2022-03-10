 package com.example.planetracker.views.ar

import android.os.Bundle
import android.os.Handler
import android.os.Looper

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card

import androidx.compose.material.Text
import androidx.compose.runtime.*

import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.planetracker.MainActivity
import com.example.planetracker.R
import com.example.planetracker.models.Plane
import com.example.planetracker.models.flight.Flight
import com.example.planetracker.repos.AeroDataBoxRepo
import com.example.planetracker.views.map.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.google.ar.sceneform.ux.ArFragment
import kotlinx.coroutines.runBlocking


 @Composable
fun ARView(arViewModel: ARViewModel, mapViewModel: MapViewModel) {
     lateinit var arFrag: ArFragment
     var mapView = rememberMapViewWithLifeCycle()
     val planes by mapViewModel.allPlanes.observeAsState()
     val headings by arViewModel.planeHeadings.observeAsState(null)
     val aimedHeading by arViewModel.aimedHeading.observeAsState(0)
     val markers: MutableList<MarkerOptions> = mutableListOf()
     var aimedPlane: Plane? by remember { mutableStateOf(null) }
     val flightCache = mutableListOf<Flight>()



     val degrees by arViewModel.degrees.observeAsState(0)

     val mainHandler = Handler(Looper.getMainLooper())

     mainHandler.post(object : Runnable {
         override fun run() {
             try{
                 if(planes != null) {
                     arViewModel.calculateHeadingsToPlanes(planes!!)
                     aimedPlane = arViewModel.findAimedPlane(headings)
                 }
             } catch (e: Exception) {
                 print(e)
             }

             mainHandler.postDelayed(this, 50)
         }
     })




    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current).also {
        it.lastLocation.addOnSuccessListener {
            arViewModel.lastKnownLocation = LatLng(it.latitude, it.longitude)
            arViewModel.mMap?.moveCamera(
                CameraUpdateFactory.newLatLng(LatLng(it.latitude,it.longitude)))
            arViewModel.mMap?.moveCamera(CameraUpdateFactory.zoomTo(7f))
        }
    }

         planes?.forEach {

             if (it.latitude != null && it.longitude != null && arViewModel.mMap != null) {

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






     Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize())
    {
        Box(contentAlignment = Alignment.Center) {
            SimpleCameraPreview()
            InfoPlane(xOffset =  aimedHeading *2, plane = aimedPlane, mapViewModel = mapViewModel)
            Box(modifier = Modifier
                .width(150.dp)
                .height(150.dp)
                .align(
                    Alignment.TopStart
                )
                .clip(CircleShape)) {
                    AndroidView(factory = {mapView}) { view ->
                        view.getMapAsync {
                            updateMarkers(model = arViewModel, mapViewModel = mapViewModel, markers = markers)

                            it.isMyLocationEnabled = true
                            it.uiSettings.isMyLocationButtonEnabled = false
                           // it.setPadding(100,0,0,0)
                            it.uiSettings.isCompassEnabled = false
                            arViewModel.mMap = it
                        }
                    }
            }

        }

    }
}

 fun getFlightInfo(mapViewModel: MapViewModel, plane: Plane): Flight? {
     var flight = MainActivity.arFlights.firstOrNull { it.aircraft?.modeS?.lowercase() == plane.icao24.lowercase() }
     if (flight != null) {
         return flight
     } else {
        return runBlocking {
             val flightres = AeroDataBoxRepo().getFlightStatus(plane.icao24)[0]
            MainActivity.arFlights.add(flightres)
            return@runBlocking flightres
         }

     }
 }

 @Composable
 fun InfoPlane(xOffset: Int, plane: Plane?, mapViewModel: MapViewModel) {
     if(plane != null) {
         val flight = getFlightInfo(mapViewModel = mapViewModel, plane = plane)
         Card(modifier = Modifier.offset(x = xOffset.dp), elevation = 5.dp) {
             Column(Modifier.padding(8.dp)) {
                 Text(flight?.aircraft?.model ?: "")
                 Text("ICAO24: " + plane.icao24)
                 Text("Heading to ${flight?.arrival?.airport?.municipalityName  ?:"???"}")
             }
         }
     }
 }

 @OptIn(ExperimentalPermissionsApi::class)
 @Composable
 fun SimpleCameraPreview() {
     val lifecycleOwner = LocalLifecycleOwner.current
     val context = LocalContext.current
     val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
     val cameraPermissionState = rememberPermissionState(
         android.Manifest.permission.CAMERA
     )
     val locationPermissionState = rememberPermissionState(
         android.Manifest.permission.ACCESS_FINE_LOCATION
     )
    LaunchedEffect(true) {
        cameraPermissionState.launchPermissionRequest()
        locationPermissionState.launchPermissionRequest()
    }



     AndroidView(
         factory = { ctx ->
             val previewView = PreviewView(ctx)
             val executor = ContextCompat.getMainExecutor(ctx)
             cameraProviderFuture.addListener({
                 val cameraProvider = cameraProviderFuture.get()
                 val preview = Preview.Builder().build().also {
                     it.setSurfaceProvider(previewView.surfaceProvider)
                 }

                 val cameraSelector = CameraSelector.Builder()
                     .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                     .build()

                 cameraProvider.unbindAll()
                 cameraProvider.bindToLifecycle(
                     lifecycleOwner,
                     cameraSelector,
                     preview
                 )
             }, executor)
             previewView
         },

         modifier = Modifier.fillMaxSize(),
     )
 }

 fun updateMarkers(model: ARViewModel, mapViewModel: MapViewModel, markers: List<MarkerOptions>) {
     val handler = Handler(Looper.getMainLooper())
     var x = 0
     val DELAY: Long = 1
     if (model.mMap != null) {
         // model.mMap!!.clear()
         markers.forEach {

             handler.postDelayed(
                 {
                     try {
                         val mark: Marker? = model.builtMarkers.firstOrNull { m -> m.title == it.title}
                         if(mark == null) {
                             val marker = model.mMap!!.addMarker(it)
                             marker!!.tag =
                                 mapViewModel.allPlanes.value?.find { plane -> plane.icao24 == it.title }
                             model.builtMarkers.add(marker)
                         } else {
                             mark.position = it.position
                         }

                     } catch (e: Exception) {
                         print(e.message)
                     }
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








