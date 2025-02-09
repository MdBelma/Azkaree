package com.ibrahim7.azkaree.model

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.ibrahim7.azkaree.R

class MyApp : Application() {

    companion object {
        var sharedRefrance: SharedPrefTheme? = null
        const val CHANNEL_ID = "channel_id"

        fun replaceFragment(f: Fragment, activity: AppCompatActivity) {
            activity.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, f).commit()
        }

        fun replaceFragmentwithNull(f: Fragment, activity: AppCompatActivity) {
            activity.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, f).addToBackStack(null).commit()
        }

        fun addbackArrow(activity: AppCompatActivity) {
            val toolbar = activity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main)
            val navigationView = activity.findViewById<View>(R.id.navigation_view)

            navigationView.visibility = View.GONE
            toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
            toolbar.setNavigationOnClickListener {
                activity.onBackPressed()
            }
        }

        fun CheckTheme(activity: AppCompatActivity) {
            sharedRefrance = SharedPrefTheme(activity)
            val toolbar = activity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main)
            val navigationView = activity.findViewById<View>(R.id.navigation_view)

            sharedRefrance?.let {
                val window = activity.window
                val controller = WindowInsetsControllerCompat(window, window.decorView)

                if (it.loadNightModeState() == true) {
                    window.statusBarColor = ContextCompat.getColor(activity, R.color.dark_status_bar)
                    controller.isAppearanceLightStatusBars = false // Dark theme, light icons

                    toolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.dark_toolbar))
                    activity.setTheme(R.style.darktheme)
                    navigationView.setBackgroundColor(ContextCompat.getColor(activity, R.color.dark_nav_view))
                } else {
                    activity.setTheme(R.style.AppTheme)
                    window.statusBarColor = ContextCompat.getColor(activity, R.color.light_status_bar)
                    controller.isAppearanceLightStatusBars = true // Light theme, dark icons

                    toolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.light_toolbar))
                    navigationView.setBackgroundColor(ContextCompat.getColor(activity, R.color.light_nav_view))
                }
            }
        }


    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Azkaree Notifications",
                NotificationManager.IMPORTANCE_HIGH // تأكد من أهمية القناة لإظهار الإشعارات بشكل واضح
            ).apply {
                description = "Channel for all Azkaree notifications"
                enableLights(true)
                enableVibration(true)
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

}