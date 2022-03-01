 package com.example.planetracker.views.ar

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.MapView

import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment


 @Composable
fun ARView(context: Context) {
     lateinit var arFrag: ArFragment
     var mapView = rememberMapViewWithLifeCycle()
     var arViewModel: ARViewModel = ARViewModel(LocalContext.current)


     var modelRenderable: ModelRenderable? = null

    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize())
    {
        Box() {
            SimpleCameraPreview()
            Box(modifier = Modifier
                .width(150.dp)
                .height(150.dp)
                .align(
                    Alignment.TopStart
                )
                .clip(CircleShape)) {
                    AndroidView(factory = {mapView}) {
                        it.getMapAsync {
                            it.isMyLocationEnabled = true
                            arViewModel.mMap = it
                        }
                    }
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








