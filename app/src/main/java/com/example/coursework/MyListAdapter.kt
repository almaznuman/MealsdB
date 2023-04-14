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

class MyListAdapter(private val context:Context,private val mylist:ArrayList<String>,private val mylist2:ArrayList<String>):RecyclerView.Adapter<MyListAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(viewgroup: ViewGroup, position: Int): MyViewHolder {
        val mylistitem=LayoutInflater.from(context).inflate(R.layout.list,viewgroup,false)
        return MyViewHolder(mylistitem)
    }

    override fun getItemCount(): Int {
        return mylist.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(mylist[position],mylist2[position])
    }

    inner class MyViewHolder(itemView:View): ViewHolder(itemView){

        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(myItem: String,myitem2:String) {
            Picasso.get().load(myitem2).into(imageView)
            itemView.findViewById<TextView>(R.id.tv).text = myItem
            itemView.setOnClickListener{
                Toast.makeText(context,myItem,Toast.LENGTH_LONG).show()
            }
        }
    }
}