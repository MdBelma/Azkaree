package com.ibrahim7.azkaree.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.databinding.AboutAppFragmentBinding // Import the generated binding class
import com.ibrahim7.azkaree.db.DatabaseHelper
import com.ibrahim7.azkaree.model.MyApp

class AboutApp : Fragment() {

    private lateinit var binding: AboutAppFragmentBinding // Declare the binding variable
    private val db by lazy {
        DatabaseHelper(requireActivity()) // Use requireActivity() to get the activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MyApp.addbackArrow(activity as AppCompatActivity)
        binding = AboutAppFragmentBinding.inflate(inflater, container, false) // Initialize the binding
        val view = binding.root // Get the root view from the binding

        binding.txtVersion.text = db.databaseName // Access views using binding
        return view
    }
}