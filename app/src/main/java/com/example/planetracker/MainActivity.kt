package com.example.planetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.planetracker.ui.theme.PlaneTrackerTheme
import com.example.planetracker.views.ar.ARView
import com.example.planetracker.views.map.GoogleMaps
import com.example.planetracker.views.map.MapViewModel
import com.google.android.gms.maps.MapView
import com.google.maps.android.compose.GoogleMap

class MainActivity : ComponentActivity() {

    val mapModel = MapViewModel()
    private lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        mapView = MapView(this)
        setContent {
            PlaneTrackerTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                Scaffold(bottomBar = {
                    BottomNavigation() {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        items.forEach { screen ->
                            BottomNavigationItem(
                                icon = {
                                    (if (screen.label == "Map") Icon(
                                        Icons.Filled.Public,
                                        contentDescription = null
                                    ) else Icon(Icons.Filled.Camera, contentDescription = null))
                                },
                                label = { Text(screen.label) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                })

                        }
                    }
                }) {
                    NavHost(navController, startDestination = Screen.Map.route) {
                        composable(Screen.Map.route) { GoogleMaps(model = mapModel) }
                        composable(Screen.AR.route) { ARView() }
                    }

                }
            }
        }
    }
}

sealed class Screen(val route: String, val label: String) {
    object Map : Screen("map", "Map")
    object AR : Screen("ar", "AR")
}

val items = listOf(
    Screen.Map,
    Screen.AR,
)


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PlaneTrackerTheme {
        Greeting("Android")
    }
}