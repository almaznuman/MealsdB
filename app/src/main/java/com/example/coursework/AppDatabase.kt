package com.example.coursework

import android.content.Context
import androidx.room.*

@Database(entities = [Meals::class], version = 1)
@TypeConverters(StringListConverter::class)
// The @TypeConverters annotation specifies a type converter to convert a List of Strings into a String.
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealsdao(): MealsDao
    // An abstract function that returns a DAO for the Meals table.

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        // The INSTANCE variable is marked as volatile to ensure that changes made by one thread are visible to all other threads.
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            // The tempInstance variable is used to check if an instance of the database already exists.

            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                // If an instance of the database does not exist, synchronize on the class object to prevent multiple threads from creating a new instance simultaneously.
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "app_database"
                ).build()
                // Create a new instance of the database using the Room.databaseBuilder() method.
                INSTANCE = instance
                return instance
            }
        }
    }
}
