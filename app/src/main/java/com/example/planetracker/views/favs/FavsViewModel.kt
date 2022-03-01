package com.example.planetracker.views.favs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.planetracker.database.PlaneDB
import com.example.planetracker.models.Plane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavsViewModel(application: Application): AndroidViewModel(application) {
    private val planeDB = PlaneDB.get(application)

    fun getFavorites(): LiveData<List<Plane>> = planeDB.planeDao().getAll()

    fun addFavorite(plane: Plane) {
        viewModelScope.launch(Dispatchers.IO) {
            planeDB.planeDao().insert(plane)
        }
    }

    fun removeFavorite(plane: Plane) {
        viewModelScope.launch(Dispatchers.IO) {
            planeDB.planeDao().delete(plane)
        }
    }

    fun removeFavoriteByIcao(icao24: String) {
        viewModelScope.launch(Dispatchers.IO) {
            planeDB.planeDao().deleteByIcao24(icao24)
        }
    }

}