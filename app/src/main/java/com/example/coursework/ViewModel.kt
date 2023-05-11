package com.example.coursework

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ViewModel: ViewModel() {
    var mealnamelist=  ArrayList<String>()
    var mealthumbnaillist = ArrayList<String>()
    var mealCategorylist = ArrayList<String>()
    var mealsList = mutableListOf<Meals>()
    var ingredientsInformation:String = ""

    // Coroutine function to search the database for meals that match a given name
    suspend fun getmealnamesfromdb(mealname: String,appDb: AppDatabase): Boolean {
        var isNotEmpty = false
        withContext(Dispatchers.IO) {
            val mealnames = appDb.mealsdao().getMealNames(mealname)
            val mealthumb = appDb.mealsdao().getMealThumb(mealname)
            val mealcategory= appDb.mealsdao().getMealcategorybymealname(mealname)
            withContext(Dispatchers.Main) {

                // If meals were found, add them to the appropriate lists
                if (mealnames.isNotEmpty()) {
                    mealnamelist.addAll(mealnames)
                    mealthumbnaillist.addAll(mealthumb)
                    mealCategorylist.addAll(mealcategory)
                    isNotEmpty = true
                }
            }
        }
        return isNotEmpty
    }

    // Coroutine function to search the database for meals that match an ingredient
    suspend fun getmealingredientfromdb(mealname: String,appDb: AppDatabase): Boolean {
        var isNotEmpty = false
        withContext(Dispatchers.IO) {
            val mealnames = appDb.mealsdao().getMealNamesByIngredient(mealname)
            val mealthumb = appDb.mealsdao().getMealThumbByIngredient(mealname)
            val mealcategory= appDb.mealsdao().getMealcategoryByIngredient(mealname)
            withContext(Dispatchers.Main) {

                // If meals were found, add them to the appropriate lists
                if (mealnames.isNotEmpty()) {
                    mealnamelist.addAll(mealnames)
                    mealthumbnaillist.addAll(mealthumb)
                    mealCategorylist.addAll(mealcategory)
                    isNotEmpty = true
                }
            }
        }
        return isNotEmpty
    }

    fun apiConnection(mealname: String): Boolean {
        var isempty = false
        val url = URL("https://www.themealdb.com/api/json/v1/1/search.php?s=$mealname")
        val con: HttpURLConnection = url.openConnection() as HttpURLConnection
        val stb = StringBuilder()
        // Use a coroutine to run the network request on a background thread
        runBlocking {
            launch {
                withContext(Dispatchers.IO) {
                    val bf = BufferedReader(InputStreamReader(con.inputStream))
                    var line: String? = bf.readLine()
                    while (line != null) {
                        stb.append(line + "\n")
                        line = bf.readLine()
                    }
                    bf.close()
                    val json = JSONObject(stb.toString())
                    // Get the "meals" array from the JSON object
                    val mealsArray: JSONArray? = json.optJSONArray("meals")
                    // If the array is null, do nothing
                    if (mealsArray == null) {
                        isempty = true
                    } else {
                        //iterate through the array and add the meal names and images to the lists
                        for (i in 0 until mealsArray.length()) {
                            val meal: JSONObject = mealsArray[i] as JSONObject
                            val mealname = meal["strMeal"] as String
                            val mealImagesource = meal["strMealThumb"] as String
                            val mealcategory=meal["strCategory"] as String
                            mealnamelist.add(mealname)
                            mealthumbnaillist.add(mealImagesource)
                            mealCategorylist.add(mealcategory)
                        }
                        stb.clear()
                    }
                }
            }
        }
        return isempty
    }
}