package com.example.planetracker.views.favs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Favs() {
    Column() {
        Card(elevation = 10.dp, modifier = Modifier.padding(16.dp).fillMaxWidth().height(70.dp)) {
            Text("Plane 1")
        }
        Card(elevation = 10.dp, modifier = Modifier.padding(16.dp).fillMaxWidth().height(70.dp)) {
            Text("Plane 2")
        }
        Card(elevation = 10.dp, modifier = Modifier.padding(16.dp).fillMaxWidth().height(70.dp)) {
            Text("Plane 3")
        }
        Card(elevation = 10.dp, modifier = Modifier.padding(16.dp).fillMaxWidth().height(70.dp)) {
            Text("Plane 4")
        }
        Card(elevation = 10.dp, modifier = Modifier.padding(16.dp).fillMaxWidth().height(70.dp)) {
            Text("Plane 5")
        }
    }
}