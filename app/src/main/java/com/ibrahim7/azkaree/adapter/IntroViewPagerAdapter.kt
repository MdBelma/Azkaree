package com.ibrahim7.azkaree.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.databinding.ScreenLayoutBinding // Import the generated binding class
import com.ibrahim7.azkaree.model.ViewPagerScreen

class IntroViewPagerAdapter(var activity: Activity, var data: MutableList<ViewPagerScreen>) : PagerAdapter() {

    private lateinit var inflater: LayoutInflater

    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ScreenLayoutBinding.inflate(inflater, container, false) // Inflate using View Binding
        val view = binding.root // Get the root view from the binding

        binding.introTitle.text = data[position].title // Access views using binding
        binding.introDescription.text = data[position].description
        binding.introImage.setImageResource(data[position].image)

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}