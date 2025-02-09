package com.ibrahim7.azkaree.adapter

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.compose.foundation.weight
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.databinding.ItemDesignCategoriesBinding
import com.ibrahim7.azkaree.databinding.ItemDesignFavoriteBinding
import com.ibrahim7.azkaree.db.DatabaseHelper
import com.ibrahim7.azkaree.model.Athker
import java.util.ArrayList

class CategoriesAdapter(
    var activity: Activity, var data: MutableList<Athker>, var layout: Int, val itemclick: onClick
) :
    RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>() {

    private val db by lazy {
        DatabaseHelper(activity)
    }

    // Use a sealed class to represent the two types of ViewHolders
    sealed class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder for item_design_categories
        class CategoriesViewHolder(val binding: ItemDesignCategoriesBinding) : MyViewHolder(binding.root) {
            val composeView: ComposeView = binding.composeView
        }

        // ViewHolder for item_design_favorite
        class FavoriteViewHolder(val binding: ItemDesignFavoriteBinding) : MyViewHolder(binding.root) {
            val composeView: ComposeView = binding.composeView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(activity)
        return when (layout) {
            R.layout.item_design_categories -> {
                val binding = ItemDesignCategoriesBinding.inflate(inflater, parent, false)
                MyViewHolder.CategoriesViewHolder(binding)
            }
            R.layout.item_design_favorite -> {
                val binding = ItemDesignFavoriteBinding.inflate(inflater, parent, false)
                MyViewHolder.FavoriteViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid layout type")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        when (holder) {
            is MyViewHolder.CategoriesViewHolder -> {
                // Set Compose content for the item
                holder.composeView.setContent {
                    CategoriesItem(item = data[position], onClick = { type ->
                        itemclick.onClickItem(position, type)
                    })
                }
                Categoriesitem(holder, position)
            }
            is MyViewHolder.FavoriteViewHolder -> {
                // Set Compose content for the item
                holder.composeView.setContent {
                    CategoriesItem(item = data[position], onClick = { type ->
                        itemclick.onClickItem(position, type)
                    })
                }
                FavoriteItem(holder, position)
            }
        }
    }

    fun updateNote(arrayList: ArrayList<Athker>, position: Int, state: Int) {
        db.setUpateAthker(
            arrayList[position].id,
            arrayList[position].status
        )
    }

    interface onClick {
        fun onClickItem(position: Int, type: Int)
    }

    companion object {
        fun copyText(position: Int, activity: Activity, arrayList: ArrayList<Athker>) {
            val clipboard =
                activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = arrayList[position].content
            Toast.makeText(activity, "تم نسخ النص", Toast.LENGTH_SHORT).show()
        }
    }

    private fun FavoriteItem(holder: MyViewHolder.FavoriteViewHolder, position: Int) {
        holder.binding.btnRemoveFromFavorite.setOnClickListener {
            if (data[position].status == 1) {
                data[position].status = 0
                updateNote(data as ArrayList<Athker>, position, 0)
                data.removeAt(position)
            }
            notifyDataSetChanged()
            itemclick.onClickItem(holder.adapterPosition, 1)
        }

        holder.binding.btnFavCopy.setOnClickListener {
            copyText(position, activity, data as ArrayList<Athker>)
        }
    }

    private fun Categoriesitem(holder: MyViewHolder.CategoriesViewHolder, position: Int) {
        holder.binding.btnClick.text = data[position].counter.toString()

        holder.binding.addToFavorite.setImageResource(
            if (data[position].status == 1) {
                R.drawable.ic_favorite_1
            } else {
                R.drawable.ic_favorite_0
            }
        )

        holder.binding.addToFavorite.setOnClickListener {
            if (data[position].status == 0) {
                data[position].status = 1
                updateNote(data as ArrayList<Athker>, position, 1)
            } else if (data[position].status == 1) {
                data[position].status = 0
                updateNote(data as ArrayList<Athker>, position, 0)
            }
            notifyDataSetChanged()
        }

        holder.binding.copy.setOnClickListener {
            copyText(position, activity, data as ArrayList<Athker>)
        }

        holder.binding.btnItem.setOnClickListener {
            holder.binding.btnClick.text = data[position].counter.toString()
            itemclick.onClickItem(position, 3)
        }

        holder.binding.btnClick.setOnClickListener {
            holder.binding.btnClick.text = data[position].counter.toString()
            Log.e("hzm", data[position].counter.toString())
            itemclick.onClickItem(position, 3)
        }
    }
}

@Composable
fun CategoriesItem(item: Athker, onClick: (Int) -> Unit) {
    var isFavorite by remember { mutableStateOf(item.status == 1) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.content)
            Text(text = "Counter: ${item.counter}")
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = {
            isFavorite = !isFavorite
            onClick(if (isFavorite) 2 else 1)
        }) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favorite",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}