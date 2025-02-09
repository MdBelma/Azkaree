package com.ibrahim7.azkaree.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.android.material.navigation.NavigationBarView
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.databinding.ActivityMainBinding
import com.ibrahim7.azkaree.fragment.AthkerTypeFragment
import com.ibrahim7.azkaree.fragment.FavoriteFragment
import com.ibrahim7.azkaree.fragment.MusbihaFragment
import com.ibrahim7.azkaree.fragment.SettingFragment
import com.ibrahim7.azkaree.model.MyApp
import com.ibrahim7.azkaree.model.MyApp.Companion.replaceFragment
import com.ibrahim7.azkaree.model.SettingSystemString
import com.ibrahim7.azkaree.receiver.MyReceiver
import com.ibrahim7.azkaree.service.MyService
import java.util.*

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val myReceiver = MyReceiver()

    private val share: SharedPreferences by lazy {
        getSharedPreferences("file", MODE_PRIVATE)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.athker -> {
                replaceFragment(AthkerTypeFragment(), this)
                return true
            }
            R.id.setting -> {
                replaceFragment(SettingFragment(), this)
                return true
            }
            R.id.mala -> {
                replaceFragment(MusbihaFragment(), this)
                return true
            }
            R.id.favorite -> {
                replaceFragment(FavoriteFragment(), this)
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MyApp.CheckTheme(this)
        replaceFragment(AthkerTypeFragment(), this)
        binding.navigationView?.setOnItemSelectedListener { item ->
            onNavigationItemSelected(item)
        }

        setSupportActionBar(binding.toolbarMain)

        setLanguage("ar", this)
        onRegisterReceiverNotification()

        // Request notification permission if needed
        requestNotificationPermission()
        if (hasNotificationPermission()) {
            startService(Intent(this, MyService::class.java))
        } else {
            requestNotificationPermission()
        }
    }
    // Helper function
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun onRegisterReceiverNotification() {
        val intentFilter = IntentFilter().apply {
            addAction(SettingSystemString.NOTIFY_DELETE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and higher, use RECEIVER_NOT_EXPORTED
            registerReceiver(myReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12, use reflection to handle the registration
            try {
                val registerReceiverMethod = Context::class.java.getMethod(
                    "registerReceiver",
                    BroadcastReceiver::class.java,
                    IntentFilter::class.java,
                    Int::class.java
                )
                val receiverNotExported = 2 // RECEIVER_NOT_EXPORTED value for Android 12
                registerReceiverMethod.invoke(this, myReceiver, intentFilter, receiverNotExported)
            } catch (_: Exception) {
                // Fallback to the old method if reflection fails
                registerReceiver(myReceiver, intentFilter)
            }
        } else {
            // For lower versions, use the old method
            registerReceiver(myReceiver, intentFilter)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_POST_NOTIFICATIONS -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Restart service if permission granted
                    startService(Intent(this, MyService::class.java))
                }
            }
        }
    }
    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1

        fun setLanguage(lan: String, context: Context) {
            val res = context.resources
            val dr = res.displayMetrics
            val cr = res.configuration
            @Suppress("DEPRECATION")
            cr.setLocale(Locale(lan))
            @Suppress("DEPRECATION")
            res.updateConfiguration(cr, dr)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MyService::class.java))
        unregisterReceiver(myReceiver)
    }
}