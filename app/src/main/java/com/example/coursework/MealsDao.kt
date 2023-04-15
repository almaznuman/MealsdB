package com.example.coursework

import androidx.room.*

//Data Access Object class for meals
@Dao
interface MealsDao {
    //query to get meal name where meal name is like the input
    @Query("SELECT meal_name FROM meals WHERE meal_name LIKE '%' || :input || '%'")
    suspend fun getMealNames(input: String): List<String>

    //query to get meal name where ingredient is like the input
    @Query("SELECT meal_name FROM meals WHERE ingredients LIKE '%' || :input || '%'")
    suspend fun getMealNamesByIngredient(input: String): List<String>

    //get thumbnail of a meal where the meal name is like the input
    @Query("SELECT mealThumb FROM meals WHERE meal_name LIKE '%' || :input || '%'")
    suspend fun getMealThumb(input: String): List<String>

    //get thumbnail of meal where ingredient is like the input
    @Query("SELECT mealThumb FROM meals WHERE ingredients LIKE '%' || :input || '%'")
    suspend fun getMealThumbByIngredient(input: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meals)

    @Delete
    suspend fun deleteMeal(meal: Meals)

    @Query("DELETE FROM meals")
    suspend fun deleteAllMeals()

}