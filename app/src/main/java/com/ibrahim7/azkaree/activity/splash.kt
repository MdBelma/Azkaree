package com.ibrahim7.azkaree.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.databinding.Splash2ActivityBinding // Import the generated binding class
import com.ibrahim7.azkaree.model.MyApp
import com.ibrahim7.azkaree.model.SharedPrefTheme

class splash : AppCompatActivity() {

    private lateinit var binding: Splash2ActivityBinding // Declare the binding variable
    var sharedRefrance: SharedPrefTheme? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Splash2ActivityBinding.inflate(layoutInflater) // Initialize the binding
        setContentView(binding.root) // Use binding.root as the content view

        MyApp.sharedRefrance = SharedPrefTheme(this)

        if (MyApp.sharedRefrance!!.mySharedPref!!.getBoolean("NightMode", false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.splash.setBackgroundColor(Color.rgb(43, 43, 43))
                binding.txt.setTextColor(Color.rgb(255, 255, 255))
                window.statusBarColor = Color.rgb(43, 43, 43)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.splash.setBackgroundColor(Color.rgb(255, 255, 255))
                window.statusBarColor = Color.rgb(255, 255, 255)
                binding.txt.setTextColor(Color.rgb(43, 43, 43))
            }
        }

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        binding.mainLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_in))
        Handler().postDelayed({
            binding.mainLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_out))
            Handler().postDelayed({
                val intenttomain = Intent(this, ViewPagerScreen::class.java)
                binding.mainLogo.visibility = View.GONE
                intenttomain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intenttomain)
                finish()
            }, 500)
        }, 700)
    }
}