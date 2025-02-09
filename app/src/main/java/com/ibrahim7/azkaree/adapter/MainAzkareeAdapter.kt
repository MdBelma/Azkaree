package com.ibrahim7.azkaree.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.databinding.ItemDesignAthkerBinding // Import the generated binding class
import com.ibrahim7.azkaree.model.TypeAthker

class MainAzkareeAdapter(
    var activity: Activity, var data: MutableList<TypeAthker>, val itemclick: onClick
) :
    RecyclerView.Adapter<MainAzkareeAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: ItemDesignAthkerBinding) : RecyclerView.ViewHolder(binding.root) {
        // The binding object is now passed to the constructor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(activity)
        val binding = ItemDesignAthkerBinding.inflate(inflater, parent, false) // Inflate using View Binding
        return MyViewHolder(binding) // Pass the binding to the ViewHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.itemTitleAzkaree.text = data[position].title // Access views using binding
        holder.binding.itemImageAzkaree.setImageResource(data[position].image)
        holder.binding.mainAzkareeCard.setOnClickListener {
            itemclick.onClickItem(holder.adapterPosition, 1)
        }
    }

    interface onClick {
        fun onClickItem(position: Int, type: Int)
    }
}