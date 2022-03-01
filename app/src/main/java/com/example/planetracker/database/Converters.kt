package com.example.planetracker.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.lang.Error
import java.lang.IllegalArgumentException

class Converters {
    @TypeConverter
    fun fromString(intListString: String): List<Int>? {
        return if(intListString.isEmpty()) {
            null
        } else
            try {
                intListString.split(",").map { it.toIntOrNull() }.requireNoNulls()
            } catch (e: IllegalArgumentException) {
                print("Sensors list contained null(s)")
                return null
            }

    }

    @TypeConverter
    fun toString(intList: List<Int>?): String {
        return intList?.joinToString(separator = ",") ?: ""
    }
}