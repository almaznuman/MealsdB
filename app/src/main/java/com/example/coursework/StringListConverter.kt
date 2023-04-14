package com.example.coursework

import androidx.room.TypeConverter

//Converts list of strings to a string and vice versa for meals database
class StringListConverter {
    @TypeConverter
    fun fromString(string: String?): List<String>? {
        return string?.split(",")
    }

    @TypeConverter
    fun toString(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}
