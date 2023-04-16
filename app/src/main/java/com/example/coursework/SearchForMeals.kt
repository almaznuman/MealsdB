package com.example.coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchForMeals : AppCompatActivity() {
    private lateinit var appDb: AppDatabase
    private lateinit var recyclerView: RecyclerView
    //button animations
    private val buttonClick = AlphaAnimation(1f, 0.8f)
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
        var model= ViewModelProvider(this).get(ViewModel::class.java)
        val myAdapter = MyListAdapter(this, model.mealnamelist, model.mealthumbnaillist,model.mealCategorylist)
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        button.setOnClickListener {
            it.startAnimation(buttonClick)

            // If the search field is blank, display a message to the user
            if (editText.text.isBlank()) {
                clearlists()
                myAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Please enter an ingredient", Toast.LENGTH_SHORT).show()

                // Otherwise, search the database for meals that match the given ingredient
            } else {
                clearlists()
                val information = editText.text.toString()
                GlobalScope.launch(Dispatchers.Main) {
                    val isNotEmpty = model.getmealnamesfromdb(information,appDb)
                    // If meals were found, update the adapter with the new data
                    if (isNotEmpty) {
                        myAdapter.notifyDataSetChanged()
                        // If no meals were found with that name, search for meals that contain that ingredient
                    } else {
                        val isNotEmpty2 = model.getmealingredientfromdb(information,appDb)
                        // If meals were found, update the adapter with the new data
                        if (isNotEmpty2) {
                            myAdapter.notifyDataSetChanged()
                            // If no meals were found, display a message to the user
                        } else {
                            clearlists()
                            Toast.makeText(
                                this@SearchForMeals,
                                "No meals found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
    private fun clearlists(){
        var model= ViewModelProvider(this).get(ViewModel::class.java)
        model.mealCategorylist.clear()
        model.mealnamelist.clear()
        model.mealthumbnaillist.clear()
    }
}