package com.ibrahim7.azkaree.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.databinding.FragmentSettingBinding
import com.ibrahim7.azkaree.databinding.UpdateDialogBinding
import com.ibrahim7.azkaree.model.MyApp
import com.ibrahim7.azkaree.model.MyApp.Companion.sharedRefrance
import com.ibrahim7.azkaree.model.SettingSystemString
import com.ibrahim7.azkaree.model.SharedPrefTheme
import java.util.Locale

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private val share by lazy {
        requireActivity().getSharedPreferences(SettingSystemString.NAME_FILE_SHER, Context.MODE_PRIVATE)
    }

    private val editor by lazy {
        share.edit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedRefrance = SharedPrefTheme(requireActivity())
        MyApp.CheckTheme(activity as AppCompatActivity)

        binding = FragmentSettingBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set up the toolbar
        val activity = requireActivity() as AppCompatActivity
        val toolbar = activity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main)
        toolbar.setNavigationIcon(null)
        activity.findViewById<View>(R.id.navigation_view).visibility = View.VISIBLE
        toolbar.title = "الإعدادات"

        // Log the current SeekBar progress
        Log.e("asdqw", (share.getLong("seekbar", share.getLong(SettingSystemString.COUNTER_NOTIFICATION, 20 * 60000)) / 60000).toInt().toString())

        // Set up the "Share App" button
        binding.shareapp.setOnClickListener {
            // رابط التطبيق على متجر Google Play
            val appPackageName = requireContext().packageName // الحصول على معرف الحزمة الخاص بالتطبيق
            val playStoreLink = "https://play.google.com/store/apps/details?id=$appPackageName"

            // التحقق من وجود التطبيق على الجهاز
            try {
                requireContext().packageManager.getPackageInfo(appPackageName, 0)

                // رسالة المشاركة
                val shareMessage = """
            تحميل تطبيق Azkaree!
            تطبيق رائع لمساعدة المستخدمين على تذكر الأذكار. حمله الآن: $playStoreLink
        """.trimIndent()

                // إنشاء نية المشاركة
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "تحميل تطبيق Azkaree!")
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                }

                // عرض نافذة المشاركة
                startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق عبر"))
            } catch (_: Exception) {
                // إذا لم يتم العثور على التطبيق، عرض رسالة خطأ
                Log.e("ShareApp", "التطبيق غير مثبت على الجهاز")
                // يمكنك عرض رسالة للمستخدم باستخدام Toast أو Snackbar
            }
        }

        // Set up the "About App" button
        binding.btnAboutApp.setOnClickListener {
            MyApp.replaceFragmentwithNull(AboutApp(), activity)
        }

        // Set up the "Show Dialog" button
        binding.showDialog.setOnClickListener {
            showUpdateDialog()
        }

        // Set up the theme switch
        if (sharedRefrance!!.loadNightModeState() == true) {
            binding.btnSwitchTheme.isChecked = true
        }

        binding.btnSwitchTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                sharedRefrance!!.setNightModeState(true)
                MyApp.replaceFragment(SettingFragment(), activity)
            } else {
                sharedRefrance!!.setNightModeState(false)
                MyApp.replaceFragment(SettingFragment(), activity)
            }
        }

        return view
    }

    private fun showUpdateDialog() {
        val mDialog = Dialog(requireActivity())
        val dialogBinding = UpdateDialogBinding.inflate(LayoutInflater.from(requireContext()))
        mDialog.setContentView(dialogBinding.root)
        mDialog.show()

        // Initialize and configure the SeekBar in the dialog
        val seekBar = dialogBinding.seekBar

        // Set the minimum value for API level 26 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.min = 3
        }

        // Set the initial progress value
        val initialProgress = (share.getLong("seekbar", share.getLong(SettingSystemString.COUNTER_NOTIFICATION, 20 * 60000)) / 60000).toInt()
        seekBar.progress = initialProgress

        // Set the initial text for the TextView using locale-aware formatting
        dialogBinding.txtTimeSBarSetting.text = String.format(Locale.getDefault(), "%d", initialProgress)

        // Set up the SeekBar listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, num: Int, p2: Boolean) {
                // Update the TextView with locale-aware formatting
                dialogBinding.txtTimeSBarSetting.text = String.format(Locale.getDefault(), "%d", num)
            }

            override fun onStartTrackingTouch(seek: SeekBar?) {
                // Optional: Handle when the user starts interacting with the SeekBar
            }

            override fun onStopTrackingTouch(seek: SeekBar?) {
                // Save the progress value using locale-aware formatting
                val progressValue = dialogBinding.txtTimeSBarSetting.text.toString().toInt()
                editor.putLong(SettingSystemString.COUNTER_NOTIFICATION, (progressValue * 60000).toLong()).apply()
            }
        })

        // Set up the "Save" button in the dialog
        dialogBinding.btnSaveEdit.setOnClickListener {
            val progressValue = dialogBinding.txtTimeSBarSetting.text.toString().toInt()
            editor.putLong("seekbar", (progressValue * 60000).toLong()).apply()
            AthkerTypeFragment.startAlam(
                requireActivity(),
                share.getLong("seekbar", 20 * 60000)
            )
            Log.e("asdqw", "seekbar")
            mDialog.dismiss()
        }
    }
}