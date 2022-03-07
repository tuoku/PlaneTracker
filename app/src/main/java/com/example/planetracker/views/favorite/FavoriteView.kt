package com.example.planetracker.views.favorite

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.rememberImagePainter
import com.example.planetracker.R
import com.example.planetracker.models.Plane
import com.example.planetracker.models.flight.Flight
import com.example.planetracker.views.map.InfoTile
import com.example.planetracker.views.map.MapViewModel
import com.example.planetracker.views.map.countryCodeToEmojiFlag
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun FavoriteView(icao: String?, mapViewModel: MapViewModel) {

    val map = rememberMapViewWithLifeCycle()

    val plane: Plane? = (mapViewModel.builtMarkers.firstOrNull { m -> m.title == icao })?.tag as Plane?
    val flight: Flight? = mapViewModel.flightCache.firstOrNull { f -> f.aircraft?.modeS?.lowercase() == icao?.lowercase() }

    val planePainter = if (flight == null) {
        painterResource(id = R.drawable.plane_placeholder)
    } else {
        rememberImagePainter(flight!!.aircraft?.image?.url ?: "")
    }
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()

    ) {
        AndroidView(factory = {map}, modifier = Modifier.height(300.dp).fillMaxWidth()) {
            it.getMapAsync {
                it.addMarker(MarkerOptions().position(LatLng(plane?.latitude ?: 0.0,plane?.longitude ?: 0.0))
                    .rotation((plane?.trueTrack ?: 0.0).toFloat())
                    .icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.aeroplane)
                    )
                    .flat(true))
                it.animateCamera(CameraUpdateFactory.newLatLng(LatLng(plane?.latitude ?: 0.0,plane?.longitude ?: 0.0)))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)

        ) {

            (if (flight != null) {
                if ((flight!!.aircraft?.image?.url ?: "").isEmpty()) {
                    Text("No image found", modifier = Modifier.align(Alignment.Center))
                } else {
                    Image(
                        painter = planePainter,
                        alignment = Alignment.TopCenter,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
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
            Text(
                flight?.aircraft?.model ?: "",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp),
                color = Color.White,
                fontSize = 32.sp
            )

        }

        LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            countryCodeToEmojiFlag(flight?.departure?.airport?.countryCode ?: "???",
                        ), fontSize = 44.sp)
                        Text(flight?.departure?.airport?.municipalityName ?: "???")
                    }
                    Text("➡", fontSize = 32.sp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            countryCodeToEmojiFlag(flight?.arrival?.airport?.countryCode ?: "???",
                        ), fontSize = 44.sp)
                        Text(flight?.arrival?.airport?.municipalityName ?: "???")
                    }
                }
            }
            item {
                InfoTile(title = "Estimated arrival time (UTC)",
                    text = flight?.arrival?.scheduledTimeUtc ?: "???")
            }
            item {
                InfoTile(title = "Operated by", text = flight?.airline?.name ?: "???")
            }
            item {
                InfoTile(title = "ICAO24", text = plane?.icao24 ?: "???")
            }
            item {
                InfoTile(title = "Callsign", text = plane?.callsign ?: "???")
            }
            item {
                InfoTile(title = "Registration", text = flight?.aircraft?.reg ?: "???")
            }

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

