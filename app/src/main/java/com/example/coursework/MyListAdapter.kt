package com.example.coursework

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso

//Reference https://youtu.be/JkFGUJyY-bQ

//class constructor receives the context as well as two list to inflate the layout with relevant meal details
class MyListAdapter(
    private val context: Context,
    private val mylist: ArrayList<String>,
    private val mylist2: ArrayList<String>,
    private val mylist3:ArrayList<String>
) : RecyclerView.Adapter<MyListAdapter.MyViewHolder>() {


    //inflates the view with an meal item
    override fun onCreateViewHolder(viewgroup: ViewGroup, position: Int): MyViewHolder {
        val mylistitem = LayoutInflater.from(context).inflate(R.layout.list, viewgroup, false)
        return MyViewHolder(mylistitem)
    }
    //returns the item count
    override fun getItemCount(): Int {
        return mylist.count()
    }
    //binds the details to the context view
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(mylist[position], mylist2[position],mylist3[position])
    }

    inner class MyViewHolder(itemView: View) : ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(myItem: String, myitem2: String,myitem3:String) {
            Picasso.get().load(myitem2).into(imageView)
            itemView.findViewById<TextView>(R.id.tv).text = myItem
            itemView.findViewById<TextView>(R.id.tv1).text = myitem3
            itemView.setOnClickListener {
                Toast.makeText(context, myItem, Toast.LENGTH_LONG).show()
            }
        }
    }
}