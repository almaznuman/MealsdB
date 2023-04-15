package com.example.coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class WebServiceSearch : AppCompatActivity() {
    private lateinit var appDb: AppDatabase
    private lateinit var lol: RecyclerView
    //button animations
    private val buttonClick = AlphaAnimation(1f, 0.8f)
    private var mealthumbnaillist = ArrayList<String>()
    private var mealnamelist = ArrayList<String>()
    private var mealCategorylist = ArrayList<String>()
    private lateinit var editText: EditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        appDb = AppDatabase.getDatabase(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recylerview)
        editText = findViewById(R.id.edit_text)
        button = findViewById(R.id.button)
        lol = findViewById(R.id.lol)

        // Create an adapter for the RecyclerView and set it
        val myAdapter = MyListAdapter(this, mealnamelist, mealthumbnaillist,mealCategorylist)
        lol.adapter = myAdapter
        lol.layoutManager = LinearLayoutManager(this)

        button.setOnClickListener {
            // Animate the button
            it.startAnimation(buttonClick)
            // If the EditText is empty, show a message
            if (editText.text.isBlank()) {
                clearlists()
                myAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Please enter an ingredient", Toast.LENGTH_SHORT).show()
            } else {
                // Otherwise, clear the lists and parse the JSON
                clearlists()
                val mealname = editText.text.toString()
                val isempty = parseJSON(mealname)
                if (isempty) {
                    Toast.makeText(this, "No meals found", Toast.LENGTH_SHORT).show()
                } else {
                    myAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun parseJSON(mealname: String): Boolean {
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
    private fun clearlists(){
        mealCategorylist.clear()
        mealnamelist.clear()
        mealthumbnaillist.clear()
    }
}
