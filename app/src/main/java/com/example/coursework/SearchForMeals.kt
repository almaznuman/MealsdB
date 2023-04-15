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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchForMeals : AppCompatActivity() {
    private lateinit var appDb: AppDatabase
    private lateinit var lol: RecyclerView
    //button animations
    private val buttonClick = AlphaAnimation(1f, 0.8f)
    private var mealthumbnaillist = ArrayList<String>()
    private var mealnamelist = ArrayList<String>()
    private lateinit var editText: EditText
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        // Get the AppDatabase instance
        appDb = AppDatabase.getDatabase(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recylerview)

        // Initialize necessary views and adapters
        editText = findViewById(R.id.edit_text)
        button = findViewById(R.id.button)
        val myAdapter = MyListAdapter(this, mealnamelist, mealthumbnaillist)
        lol = findViewById(R.id.lol)
        lol.adapter = myAdapter
        lol.layoutManager = LinearLayoutManager(this)
        button.setOnClickListener {
            it.startAnimation(buttonClick)

            // If the search field is blank, display a message to the user
            if (editText.text.isBlank()) {
                mealthumbnaillist.clear()
                mealnamelist.clear()
                myAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Please enter an ingredient", Toast.LENGTH_SHORT).show()

                // Otherwise, search the database for meals that match the given ingredient
            } else {
                mealthumbnaillist.clear()
                mealnamelist.clear()
                val information = editText.text.toString()
                GlobalScope.launch(Dispatchers.Main) {
                    val isNotEmpty = getmealnamesfromdb(information)
                    // If meals were found, update the adapter with the new data
                    if (isNotEmpty) {
                        myAdapter.notifyDataSetChanged()
                        // If no meals were found with that name, search for meals that contain that ingredient
                    } else {
                        val isNotEmpty2 = getmealingredientfromdb(information)
                        // If meals were found, update the adapter with the new data
                        if (isNotEmpty2) {
                            myAdapter.notifyDataSetChanged()
                            // If no meals were found, display a message to the user
                        } else {
                            Toast.makeText(
                                this@SearchForMeals,
                                "No meals found",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        }
    }

    // Coroutine function to search the database for meals that match a given name
    private suspend fun getmealnamesfromdb(mealname: String): Boolean {
        var isNotEmpty = false
        withContext(Dispatchers.IO) {
            val mealnames = appDb.mealsdao().getMealNames(mealname)
            val mealthumb = appDb.mealsdao().getMealThumb(mealname)
            withContext(Dispatchers.Main) {

                // If meals were found, add them to the appropriate lists
                if (mealnames.isNotEmpty()) {
                    mealnamelist.addAll(mealnames)
                    mealthumbnaillist.addAll(mealthumb)
                    isNotEmpty = true
                }
            }
        }
        return isNotEmpty
    }

    // Coroutine function to search the database for meals that match an ingredient
    private suspend fun getmealingredientfromdb(mealname: String): Boolean {
        var isNotEmpty = false
        withContext(Dispatchers.IO) {
            val mealnames = appDb.mealsdao().getMealNamesByIngredient(mealname)
            val mealthumb = appDb.mealsdao().getMealThumbByIngredient(mealname)
            withContext(Dispatchers.Main) {

                // If meals were found, add them to the appropriate lists
                if (mealnames.isNotEmpty()) {
                    mealnamelist.addAll(mealnames)
                    mealthumbnaillist.addAll(mealthumb)
                    isNotEmpty = true
                }
            }
        }
        return isNotEmpty
    }
}