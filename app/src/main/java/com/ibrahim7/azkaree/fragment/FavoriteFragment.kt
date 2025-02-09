package com.ibrahim7.azkaree.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.adapter.CategoriesAdapter
import com.ibrahim7.azkaree.databinding.FragmentFavoriteBinding // Import the generated binding class
import com.ibrahim7.azkaree.db.DatabaseHelper
import com.ibrahim7.azkaree.model.Athker

class FavoriteFragment : Fragment(), CategoriesAdapter.onClick {

    private lateinit var binding: FragmentFavoriteBinding // Declare the binding variable
    private val data by lazy {
        ArrayList<Athker>()
    }
    private val adapter by lazy {
        CategoriesAdapter(requireActivity(), data, R.layout.item_design_favorite, this)
    }
    private val db by lazy {
        DatabaseHelper(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false) // Initialize the binding
        val view = binding.root // Get the root view from the binding
       /* (activity as AppCompatActivity).toolbar_main.title = "المفضلة"*/
        val activity = requireActivity() as AppCompatActivity
        val toolbar = activity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main)
        toolbar.title = "المفضلة"

        for (i in db.getAllData()) {
            if (i.status == 1) {
                data.add(i)
            }
        }
        Log.e("www", db.getAllData().toString())

        if (data.isNotEmpty()) {
            binding.favoriteRecyclerView.visibility = View.VISIBLE // Use binding to access the RecyclerView
            binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(activity) // Use binding to access the RecyclerView
            binding.favoriteRecyclerView.setHasFixedSize(true) // Use binding to access the RecyclerView
            binding.favoriteRecyclerView.adapter = adapter // Use binding to access the RecyclerView
        } else {
            binding.favoriteRecyclerView.visibility = View.GONE // Use binding to access the RecyclerView
            binding.favLogo.visibility = View.VISIBLE // Use binding to access the ImageView
            binding.favTxtnote.visibility = View.VISIBLE // Use binding to access the TextView
        }
        return view
    }

    override fun onClickItem(position: Int, type: Int) {
        when (type) {
            1 -> {
                if (data.isEmpty()) {
                    binding.favoriteRecyclerView.visibility = View.GONE // Use binding to access the RecyclerView
                    binding.favLogo.visibility = View.VISIBLE // Use binding to access the ImageView
                    binding.favTxtnote.visibility = View.VISIBLE // Use binding to access the TextView
                }
            }
        }
    }
}