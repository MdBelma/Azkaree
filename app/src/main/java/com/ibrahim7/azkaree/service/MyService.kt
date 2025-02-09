package com.ibrahim7.azkaree.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.activity.MainActivity
import com.ibrahim7.azkaree.model.MyApp
import com.ibrahim7.azkaree.model.SettingSystemString
import com.ibrahim7.azkaree.receiver.BroadcastReceiverNotifictionAlarm.Companion.NOTIFICATION_ID

class MyService : Service() {

    private val share by lazy {
        getSharedPreferences(SettingSystemString.NAME_FILE_SHER, MODE_PRIVATE)
    }

    private val editor by lazy {
        share.edit()
    }

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // You might initialize resources here, but keep it lightweight
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val mediaResourceId = it.getIntExtra(SettingSystemString.MEDIA, 0)
            val nameTypeAthker = it.getStringExtra(SettingSystemString.NAME_TYPE_ATHKER)

            if (mediaResourceId != 0 && nameTypeAthker != null) {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(this, mediaResourceId)
                mediaPlayer?.start()

                val notification = createNotification(this, nameTypeAthker)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK or ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                    )
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }

                getAction(it, this)
            }
        }

        return START_NOT_STICKY
    }

    fun getAction(intent: Intent, context: Context) {
        if (intent.action == SettingSystemString.NOTIFY_DELETE) {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            context.stopService(Intent(context, MyService::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    private fun createNotification(context: Context, title: String): Notification {
        val expandedView = RemoteViews(
            context.packageName,
            if (MyApp.sharedRefrance!!.loadNightModeState() == true) {
                R.layout.notification
            } else {
                R.layout.notification_azkar_sound
            }
        )

        val deleteIntent = Intent(SettingSystemString.NOTIFY_DELETE).apply {
            putExtra("SOURCE", "NOTIFICATION_DELETE")
            `package` = context.packageName
        }

        val deletePendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val deletePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            deleteIntent,
            deletePendingIntentFlags
        )
        expandedView.setOnClickPendingIntent(R.id.btnDelete, deletePendingIntent)

        val notifyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, pendingIntentFlags)

        val notificationBuilder = NotificationCompat.Builder(context, MyApp.CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher)
            .setAutoCancel(true)
            .setCustomContentView(expandedView)

        expandedView.setTextViewText(R.id.textSongName, title)

        return notificationBuilder.build()
    }
}