 package com.example.planetracker.views.ar

import android.content.Context
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState

import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment


 @Composable
fun ARView(context: Context) {
     lateinit var arFrag: ArFragment

     var modelRenderable: ModelRenderable? = null

    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize())
    {
       SimpleCameraPreview()
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
    LaunchedEffect(true) {
        cameraPermissionState.launchPermissionRequest()
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






