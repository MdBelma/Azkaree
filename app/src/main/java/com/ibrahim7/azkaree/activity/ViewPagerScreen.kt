package com.ibrahim7.azkaree.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.adapter.IntroViewPagerAdapter
import com.ibrahim7.azkaree.databinding.ViewpagerScreenBinding
import com.ibrahim7.azkaree.model.SettingSystemString
import com.ibrahim7.azkaree.model.ViewPagerScreen
import java.util.Locale

class ViewPagerScreen : AppCompatActivity() {

    private lateinit var binding: ViewpagerScreenBinding
    private val data by lazy { ArrayList<ViewPagerScreen>() }
    private val share by lazy { getSharedPreferences(FILE_PREFS, MODE_PRIVATE) }
    private val editor by lazy { share.edit() }

    // Constants for shared preferences and SeekBar
    private companion object {
        const val PREFS_NAME = "myPrefs"
        const val IS_INTRO_OPENED = "isIntroOpened"
        const val COUNTER_NOTIFICATION = "counter_notification"
        const val FILE_PREFS = "file"
        const val MIN_SEEKBAR_VALUE = 3
        const val MILLISECONDS_MULTIPLIER = 60000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewpagerScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable full-screen mode
        enableFullScreenMode()

        // Initialize and configure the SeekBar
        val seekBar = binding.seekBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.min = MIN_SEEKBAR_VALUE
        }
        seekBar.progress = MIN_SEEKBAR_VALUE

        // Set up SeekBar listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, num: Int, p2: Boolean) {
                binding.txtTimeSBar.text = String.format(Locale.getDefault(), "%d", num)
            }

            override fun onStartTrackingTouch(seek: SeekBar?) {
                // Optional: Handle when the user starts interacting with the SeekBar
            }

            override fun onStopTrackingTouch(seek: SeekBar?) {
                editor.putLong(COUNTER_NOTIFICATION, (binding.txtTimeSBar.text.toString().toInt() * MILLISECONDS_MULTIPLIER).toLong()).apply()
            }
        })

        // Check if the intro has been shown before
        checkViewPagerGetStarted()

        // Set language to Arabic
        MainActivity.setLanguage("ar", this)

        // Load data for the ViewPager
        getViewpagerData()

        // Set up the ViewPager adapter
        val introViewPagerAdapter = IntroViewPagerAdapter(this, data)
        binding.viewPager.adapter = introViewPagerAdapter

        // Link the TabLayout with the ViewPager
        binding.tabIndicator.setupWithViewPager(binding.viewPager)

        // Handle "Next" button click
        binding.btnNext.setOnClickListener {
            var position = binding.viewPager.currentItem
            if (position < data.size) {
                position++
                binding.viewPager.currentItem = position
            }

            if (position == data.size - 1) {
                loadLastScreen()
            }
        }

        // Handle "Get Started" button click
        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            editor.putBoolean(SettingSystemString.START_ALARM, true).apply()
            savePrefsData()
            finish()
        }

        // Handle TabLayout tab selection
        binding.tabIndicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == data.size - 1) {
                    loadLastScreen()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                binding.tabIndicator.visibility = View.VISIBLE
                binding.btnNext.visibility = View.VISIBLE
                binding.btnGetStarted.visibility = View.GONE
                binding.dialog2.visibility = View.GONE
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Handle tab reselection
            }
        })
    }

    /**
     * Enable full-screen mode using the modern API.
     */
    private fun enableFullScreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // For older versions
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    /**
     * Check if the intro has been shown before.
     * If yes, skip to the MainActivity.
     */
    private fun checkViewPagerGetStarted() {
        if (restorePref()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    /**
     * Restore preferences to check if the intro has been shown before.
     */
    private fun restorePref(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getBoolean(IS_INTRO_OPENED, false)
    }

    /**
     * Save preferences to indicate that the intro has been shown.
     */
    private fun savePrefsData() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(IS_INTRO_OPENED, true)
        editor.apply()
    }

    /**
     * Load the last screen of the ViewPager.
     */
    private fun loadLastScreen() {
        binding.btnGetStarted.visibility = View.VISIBLE
        binding.tabIndicator.visibility = View.GONE
        binding.dialog2.visibility = View.VISIBLE
        binding.btnNext.visibility = View.GONE
        binding.btnGetStarted.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_animation))
    }

    /**
     * Populate the ViewPager data.
     */
    private fun getViewpagerData() {
        data.add(
            ViewPagerScreen(
                "تطبيق أذكاري", "يجعلك تعيش في تجربة إيمانية , و رؤيا نورانية\n" +
                        "في روحانيات الأذكار", R.drawable.ic_viewpager_back2
            )
        )
        data.add(
            ViewPagerScreen(
                "أذكاري هي حياتي", "ردد أذكارك صباحا و مساءا , تذكر نعم ربك \n" +
                        "عليك مع تطبيق أذكار", R.drawable.ic_background2
            )
        )
        data.add(ViewPagerScreen("", "", R.drawable.ic_background3))
    }
}