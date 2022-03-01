package com.example.planetracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.planetracker.models.Plane

@Database(entities = [(Plane::class)], version = 1)
@TypeConverters(Converters::class)
abstract class PlaneDB: RoomDatabase() {

    abstract fun planeDao(): PlaneDao

    companion object {
        private var sInstance: PlaneDB? = null
        @Synchronized
        fun get(context: Context): PlaneDB {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(
                    context.applicationContext,
                    PlaneDB::class.java,
                    "planes.db"
                ).build()
            }
            return sInstance!!
        }
    }

}