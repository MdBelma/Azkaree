package com.ibrahim7.azkaree.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ibrahim7.azkaree.service.MyService
import com.ibrahim7.azkaree.model.SettingSystemString

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            SettingSystemString.NOTIFY_DELETE -> {
                // Handle notification dismissal/delete action
                context.stopService(Intent(context, MyService::class.java))
            }
            else -> {
                // Forward other actions to the service
                val serviceIntent = Intent(context, MyService::class.java).apply {
                    action = intent.action
                    putExtras(intent)
                }

                // Start the service with Android 8+ compatibility
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}