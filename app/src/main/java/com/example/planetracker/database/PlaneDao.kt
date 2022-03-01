package com.example.planetracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.planetracker.models.Plane

@Dao
interface PlaneDao {

    @Query("SELECT * FROM plane")
    fun getAll(): LiveData<List<Plane>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plane: Plane): Long

    @Delete
    suspend fun delete(plane: Plane)

    @Query("DELETE FROM plane WHERE icao24 = :icao24")
    fun deleteByIcao24(icao24: String)
}