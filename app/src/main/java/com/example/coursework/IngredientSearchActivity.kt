package com.example.coursework

import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class IngredientSearchActivity : AppCompatActivity() {

    // Declare the properties of the class
    private lateinit var appDb: AppDatabase
    private val mealsList = mutableListOf<Meals>()
    private lateinit var button: Button
    private lateinit var button3: Button
    private lateinit var scrollView2: ScrollView
    private lateinit var editText: EditText
    private lateinit var tv: TextView

    // button animations
    private val buttonClick = AlphaAnimation(1f, 0.8f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for the activity
        setContentView(R.layout.activity_ingredient_search)

        // Initialize the app database
        appDb = AppDatabase.getDatabase(this)

        // Create a StringBuilder object to hold the JSON response
        val stb = StringBuilder()

        // Get references to the UI elements
        button = findViewById(R.id.button)
        button3 = findViewById(R.id.button3)
        scrollView2 = findViewById(R.id.scrollView2)
        editText = findViewById(R.id.edit_text)
        tv = findViewById(R.id.tv)

        // Set an OnClickListener for the "Search" button
        button.setOnClickListener {
            it.startAnimation(buttonClick)
            scrollView2.scrollTo(0, 0)
            if (editText.text.isBlank()) {
                // If the search box is blank, show a Toast message
                Toast.makeText(this, "Please enter an ingredient", Toast.LENGTH_SHORT).show()
            } else {
                // If the search box is not blank, get the user's input and construct a URL to call the API
                val ingredient = editText.text.toString()
                val url = URL("https://www.themealdb.com/api/json/v1/1/filter.php?i=$ingredient")
                val con: HttpURLConnection = url.openConnection() as HttpURLConnection
                runBlocking {
                    launch {
                        // Use coroutines to run the network request on a background thread
                        withContext(Dispatchers.IO) {
                            // Read the JSON response and add each meal to the mealsList
                            val bf = BufferedReader(InputStreamReader(con.inputStream))
                            var line: String? = bf.readLine()
                            while (line != null) {
                                stb.append(line + "\n")
                                line = bf.readLine()
                            }
                            bf.close()
                            apiConnection(stb)
                            stb.clear()
                        }
                    }
                }
            }
        }

        // Set an OnClickListener for the "Save" button
        button3.setOnClickListener {
            it.startAnimation(buttonClick)
            if (mealsList.size > 0) {
                // If there are meals in the mealsList, add each meal to the database
                for (meal in mealsList) {
                    runBlocking {
                        launch {
                            withContext(Dispatchers.IO) {
                                appDb.mealsdao().insertMeal(meal)
                            }
                        }
                    }
                }
                // Show a Toast message to indicate that meals were added
                Toast.makeText(this, "Meals Added", Toast.LENGTH_SHORT).show()
            } else {
                // If there are no meals in the mealsList, show a Toast message
                Toast.makeText(this, "No meals to save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun apiConnection(stb: StringBuilder) {
        // Convert StringBuilder to JSONObject
        val json = JSONObject(stb.toString())
        val allmeals = StringBuilder()
        // Retrieve meals array from JSONObject
        val mealsArray: JSONArray? = json.optJSONArray("meals")

        if (mealsArray == null) {
            allmeals.append("No meals found")
        } else {
            // Loop through each meal object in the meals array
            for (i in 0 until mealsArray.length()) {
                // Retrieve the current meal object and its name
                val meal: JSONObject = mealsArray[i] as JSONObject
                val mealname = meal["strMeal"] as String
                allmeals.append("${i + 1}) $mealname \n\n")
                // Create a URL object for the current meal's API endpoint
                val url = URL("https://www.themealdb.com/api/json/v1/1/search.php?s=$mealname")
                val con: HttpURLConnection = url.openConnection() as HttpURLConnection
                val sb = StringBuilder()
                // Use coroutines to launch a background task for fetching the meal data
                runBlocking {
                    launch {
                        withContext(Dispatchers.IO) {
                            val bf = BufferedReader(InputStreamReader(con.inputStream))
                            var line: String? = bf.readLine()
                            while (line != null) {
                                sb.append(line + "\n")
                                line = bf.readLine()
                            }
                            bf.close()
                        }
                    }
                }
                // Parse the retrieved meal data as a JSONObject
                val mealJson = JSONObject(sb.toString())
                val mealArray: JSONArray = mealJson.getJSONArray("meals")
                val mealObject: JSONObject = mealArray[0] as JSONObject

                // Append all relevant data to the StringBuilder object
                allmeals.append("Drink Alternate: ${mealObject["strDrinkAlternate"]}\n")
                allmeals.append("Category: ${mealObject["strCategory"]}\n")
                allmeals.append("Area: ${mealObject["strArea"]}\n")
                allmeals.append("Instructions: ${mealObject["strInstructions"]}\n")
                allmeals.append("Tags: ${mealObject["strTags"]}\n")
                allmeals.append("YouTube: ${mealObject["strYoutube"]}\n")
                allmeals.append("Ingredients:\n")
                // Append all ingredients and their measurements to the StringBuilder object
                val ingredientlist = mutableListOf<String>()
                val measureslist = mutableListOf<String>()
                for (j in 1..20) {
                    val ingredient = mealObject["strIngredient$j"]
                    val measure = mealObject["strMeasure$j"]
                    if (ingredient != "" && measure != "") {
                        allmeals.append("$ingredient: $measure\n")
                        ingredientlist.add(ingredient.toString())
                        measureslist.add(measure.toString())
                    }
                }
                allmeals.append("\n")
                // Create a new Meals object and add it to a list
                mealsList.add(
                    Meals(
                        mealObject["idMeal"].toString().toInt(),
                        mealObject["strMeal"].toString(),
                        mealObject["strDrinkAlternate"].toString(),
                        mealObject["strCategory"].toString(),
                        mealObject["strArea"].toString(),
                        mealObject["strInstructions"].toString(),
                        mealObject["strMealThumb"].toString(),
                        mealObject["strTags"].toString(),
                        mealObject["strYoutube"].toString(),
                        ingredientlist,
                        measureslist,
                        mealObject["strSource"].toString(),
                        mealObject["strImageSource"].toString(),
                        mealObject["strCreativeCommonsConfirmed"].toString(),
                        mealObject["dateModified"].toString()
                    )
                )
            }
        }//adding to the UI textview
        runOnUiThread {
            tv.text = allmeals.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mealsList.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("data", tv.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        tv.setText(savedInstanceState.getString("data"))
    }
}