package com.example.planetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.planetracker.apis.AeroDataBoxAPI
import com.example.planetracker.ui.theme.PlaneTrackerTheme
import com.example.planetracker.views.ar.ARView
import com.example.planetracker.views.favs.FavsView
import com.example.planetracker.views.favs.FavsViewModel
import com.example.planetracker.views.map.GoogleMaps
import com.example.planetracker.views.map.MapViewModel
import com.google.android.gms.maps.MapView

class MainActivity : ComponentActivity() {

    val mapModel = MapViewModel()
    private lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AeroDataBoxAPI.setContext(this)

        favsViewModel = FavsViewModel(application)

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
                                    when(screen.label) {
                                        "Map" -> Icon(
                                            Icons.Filled.Public,
                                            contentDescription = null
                                        )
                                        "Favorites" -> Icon(
                                            Icons.Filled.Favorite,
                                            contentDescription = null
                                        )
                                        "AR" -> Icon(Icons.Filled.Camera, contentDescription = null)
                                    }

                                },
                                label = { Text(screen.label) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {

                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }

                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                })

                        }
                    }
                }) {
                    NavHost(navController, startDestination = Screen.Map.route) {
                        composable(Screen.Map.route) { GoogleMaps(model = mapModel, favsViewModel = favsViewModel) }
                        composable(Screen.Favorites.route) { FavsView(favsViewModel) }
                        composable(Screen.AR.route) { ARView(LocalContext.current) }
                    }

                }
            }
        }
    }

    companion object {
        private lateinit var favsViewModel: FavsViewModel

    }
}

sealed class Screen(val route: String, val label: String) {
    object Map : Screen("map", "Map")
    object Favorites: Screen("favs", "Favorites")
    object AR : Screen("ar", "AR")
}

val items = listOf(
    Screen.Map,
    Screen.Favorites,
    Screen.AR,
)
