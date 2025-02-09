package com.ibrahim7.azkaree.fragment

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.adapter.MainAzkareeAdapter
import com.ibrahim7.azkaree.databinding.FragmentAthkerBinding
import com.ibrahim7.azkaree.model.MyApp
import com.ibrahim7.azkaree.model.SettingSystemString
import com.ibrahim7.azkaree.model.TypeAthker
import com.ibrahim7.azkaree.receiver.BroadcastReceiverNotifictionAlarm
import com.ibrahim7.azkaree.service.MyService
import java.text.SimpleDateFormat
import java.util.*

class AthkerTypeFragment : Fragment(), MainAzkareeAdapter.onClick {

    private lateinit var binding: FragmentAthkerBinding
    private val bundle = Bundle()
    private val data = ArrayList<TypeAthker>()

    private val share by lazy {
        requireActivity().getSharedPreferences(SettingSystemString.NAME_FILE_SHER, Context.MODE_PRIVATE)
    }

    private val adapter by lazy {
        MainAzkareeAdapter(requireActivity(), data, this)
    }

    private val intentService by lazy {
        Intent(requireActivity(), MyService::class.java)
    }

    private var isStartServiceS = false
    private var isStartServiceM = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getTypeData()

        if (share.getBoolean(SettingSystemString.START_ALARM, true)) {
            startAlam(
                requireActivity(),
                share.getLong(SettingSystemString.COUNTER_NOTIFICATION, 20 * 60000)
            )
            Log.e("Alarm", "COUNTER_NOTIFICATION")
            share.edit().putBoolean(SettingSystemString.START_ALARM, false).apply()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAthkerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       /* (activity as AppCompatActivity).toolbar_main.setNavigationIcon(null)
        (activity as AppCompatActivity).navigation_view.visibility = View.VISIBLE
        (activity as AppCompatActivity).toolbar_main.title = getText(R.string.app_name)*/
        val activity = requireActivity() as AppCompatActivity
        val toolbar = activity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main)

        toolbar.setNavigationIcon(null)
        activity.findViewById<View>(R.id.navigation_view).visibility = View.VISIBLE
        toolbar.title = getText(R.string.app_name)
        getTime()

        binding.mainAzkareeRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = this@AthkerTypeFragment.adapter
        }

        binding.btnMorningSound.setOnClickListener {
            toggleService(R.raw.s, "أذكار الصباح مسموعة", isStartServiceS).also { isStartServiceS = it }
        }

        binding.btnEveningSound.setOnClickListener {
            toggleService(R.raw.m, "أذكار المساء مسموعة", isStartServiceM).also { isStartServiceM = it }
        }
    }

    override fun onClickItem(position: Int, type: Int) {
        when (type) {
            1 -> sendData(position)
        }
    }

    private fun getTime() {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val timeNum = time.substring(0, 2).toInt()

        if (timeNum > 12) {
            binding.imageTime.setImageResource(R.drawable.ic_moon)
            binding.txtTime.text = "أسعد الله مسائكم"
        } else {
            binding.imageTime.setImageResource(R.drawable.ic_sun)
            binding.txtTime.text = "أسعد الله صباحكم"
        }
    }

    private fun sendData(position: Int) {
        bundle.putInt("type", position + 1)
        bundle.putString("title", data[position].title)
        val ahker = AzkareeDataFragment().apply { arguments = bundle }
        MyApp.replaceFragmentwithNull(ahker, activity as AppCompatActivity)
    }

    private fun getTypeData() {
        data.apply {
            add(TypeAthker("أذكار الصباح", R.drawable.ic_morning))
            add(TypeAthker("أذكار المساء", R.drawable.ic_night))
            add(TypeAthker("أذكار النوم", R.drawable.ic_bedtime))
            add(TypeAthker("أذكار الاستيقاظ من النوم", R.drawable.ic_wakeup))
            add(TypeAthker("اذكار قبل الدخول الى المسجد", R.drawable.ic_mosque))
        }
    }

    private fun toggleService(media: Int, name: String, isServiceRunning: Boolean): Boolean {
        if (isServiceRunning) {
            requireActivity().stopService(intentService)
            return false
        } else {
            startService(media, name)
            return true
        }
    }

    private fun startService(media: Int, name: String) {
        intentService.apply {
            putExtra(SettingSystemString.MEDIA, media)
            putExtra(SettingSystemString.NAME_TYPE_ATHKER, name)
        }
        ContextCompat.startForegroundService(requireActivity(), intentService)
    }



    companion object {
        fun startAlam(context: Context, time: Long) {
            val i = Intent(context, BroadcastReceiverNotifictionAlarm::class.java)

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getBroadcast(context, 0, i, flags)

            val alarm = context.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            alarm.setRepeating(
                AlarmManager.RTC_WAKEUP,
                time + System.currentTimeMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }
}