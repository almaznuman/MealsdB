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

class WebServiceSearch : AppCompatActivity() {
    private lateinit var appDb: AppDatabase
    private lateinit var recyclerView: RecyclerView
    //button animations
    private val buttonClick = AlphaAnimation(1f, 0.8f)
    private lateinit var editText: EditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        appDb = AppDatabase.getDatabase(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recylerview)
        editText = findViewById(R.id.edit_text)
        button = findViewById(R.id.button)
        recyclerView = findViewById(R.id.recyclerview)
        val model= ViewModelProvider(this).get(ViewModel::class.java)
        // Create an adapter for the RecyclerView and set it
        val myAdapter = MyListAdapter(this, model.mealnamelist, model.mealthumbnaillist,model.mealCategorylist)
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

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
                val isempty = model.apiConnection(mealname)
                if (isempty) {
                    Toast.makeText(this, "No meals found", Toast.LENGTH_SHORT).show()
                } else {
                    myAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun clearlists(){
        val model= ViewModelProvider(this).get(ViewModel::class.java)
        model.mealCategorylist.clear()
        model.mealnamelist.clear()
        model.mealthumbnaillist.clear()
    }
}
