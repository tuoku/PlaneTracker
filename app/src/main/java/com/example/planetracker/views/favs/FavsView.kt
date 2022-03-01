package com.example.planetracker.views.favs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FavsView(viewModel: FavsViewModel) {
    val favsList = viewModel.getFavorites().observeAsState(listOf())
    LazyColumn {
        items(favsList.value) {
            Card(elevation = 10.dp, modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(70.dp)) {
                Text(it.toString())
            }
        }

    }
}