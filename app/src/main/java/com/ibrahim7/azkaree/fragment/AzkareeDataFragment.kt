package com.ibrahim7.azkaree.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.adapter.CategoriesAdapter
import com.ibrahim7.azkaree.databinding.CategoriesFragmentBinding // Import the generated binding class
import com.ibrahim7.azkaree.db.DatabaseHelper
import com.ibrahim7.azkaree.model.Athker
import com.ibrahim7.azkaree.model.MyApp
import java.util.*
import kotlin.text.get

class AzkareeDataFragment : Fragment(), CategoriesAdapter.onClick {

    private lateinit var binding: CategoriesFragmentBinding // Declare the binding variable
    private val data by lazy {
        ArrayList<Athker>()
    }
    private val adapter by lazy {
        CategoriesAdapter(requireActivity(), data, R.layout.item_design_categories, this)
    }
    private val db by lazy {
        DatabaseHelper(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MyApp.addbackArrow(activity as AppCompatActivity)
        binding = CategoriesFragmentBinding.inflate(inflater, container, false) // Initialize the binding
        val view = binding.root // Get the root view from the binding
        val type = arguments
       /* (activity as AppCompatActivity).toolbar_main.title = type?.get("title").toString()*/

        val activity = requireActivity() as AppCompatActivity
        val toolbar = activity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main)
        toolbar.title = type?.get("title").toString()
        for (i in db.getAllData()) {
            if (i.type == type?.get("type")) {
                data.add(Athker(i.id, i.content, i.counter, i.status, i.type))
            }
        }

        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(activity) // Use binding to access the RecyclerView
        binding.categoriesRecyclerView.adapter = adapter // Use binding to access the RecyclerView

        return view
    }

    override fun onClickItem(position: Int, type: Int) {
        when (type) {
            3 -> {
                CounterIsFinsihed(position)
            }
        }
    }

    private fun CounterIsFinsihed(position: Int) {
        data[position].counter--
        if (data[position].counter == 0) {
            data.removeAt(position)
        }
        adapter.notifyDataSetChanged()
    }
}