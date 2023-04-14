package com.example.coursework

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
//Dataclass for meals
@Entity(tableName = "meals")
data class Meals(
    @ColumnInfo(name = "id")  val id: Int?,
    @PrimaryKey @ColumnInfo(name = "meal_name") val mealName: String,
    @ColumnInfo(name = "drink_Alternate") val drinkAlternate: String?,
    @ColumnInfo(name = "category") val category: String?,
    @ColumnInfo(name = "area") val area: String?,
    @ColumnInfo(name = "instructions") val instructions: String?,
    @ColumnInfo(name = "mealThumb") val mealThumb: String?,
    @ColumnInfo(name = "tags") val tags: String?,
    @ColumnInfo(name = "youtube") val youtube: String?,
    @ColumnInfo(name = "ingredients") val ingredients: List<String>?,
    @ColumnInfo(name = "measures") val measures: List<String>?,
    @ColumnInfo(name = "source") val source: String?,
    @ColumnInfo(name = "imageSource") val imageSource: String?,
    @ColumnInfo(name = "creativeCommonsConfirmed") val creativeCommonsConfirmed: String?,
    @ColumnInfo(name = "dateModified") val dateModified: String?
)


