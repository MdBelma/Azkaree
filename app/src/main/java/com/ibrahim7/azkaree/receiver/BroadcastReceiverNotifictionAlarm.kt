package com.ibrahim7.azkaree.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.ibrahim7.azkaree.R
import com.ibrahim7.azkaree.activity.MainActivity
import com.ibrahim7.azkaree.fragment.AthkerTypeFragment
import com.ibrahim7.azkaree.model.MyApp
import com.ibrahim7.azkaree.model.SettingSystemString
import com.ibrahim7.azkaree.utils.DateTimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BroadcastReceiverNotifictionAlarm : BroadcastReceiver() {

    private lateinit var share: SharedPreferences

    override fun onReceive(context: Context, intent: Intent) {
        share = context.getSharedPreferences(SettingSystemString.NAME_FILE_SHER, Context.MODE_PRIVATE)

        // Get current time in Arabic locale in 12-hour format
        val currentTime = DateTimeUtils.getCurrentTime()

        createNotification(context, getData(context), currentTime)

        CoroutineScope(Dispatchers.IO).launch {
            AthkerTypeFragment.startAlam(
                context,
                share.getLong(SettingSystemString.COUNTER_NOTIFICATION, 20 * 60000)
            )
        }
    }

    private fun createNotification(context: Context, content: String, time: String) {
        val expandedView = RemoteViews(
            context.packageName,
            if (MyApp.sharedRefrance?.loadNightModeState() == true) {
                R.layout.notification_alam_night_mode
            } else {
                R.layout.notification_alam
            }
        )

        // Set the text content and time
        expandedView.setTextViewText(R.id.txtContentNotificationAlarm, content)
        expandedView.setTextViewText(R.id.txtTimeNotificationAlarm, time)

        // Create delete intent
        val deleteIntent = Intent(SettingSystemString.NOTIFY_DELETE)
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

        // Create notification
      /*  val notificationBuilder = NotificationCompat.Builder(context, SettingSystemString.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_play)
            .setAutoCancel(true)
            .setCustomContentView(expandedView)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)*/

        val notificationBuilder = NotificationCompat.Builder(context,SettingSystemString.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setAutoCancel(true)
            .setCustomContentView(expandedView)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        // Create content intent
        val notifyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notifyIntent,
            pendingIntentFlags
        )
        notificationBuilder.setContentIntent(pendingIntent)

        // Create or update notification channel for Android O and above
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SettingSystemString.CHANNEL_ID,
                "Azkar Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for Azkar notifications"
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Show notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getData(context: Context): String {
        val editor = share.edit()
        val data = arrayOf(
            "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            "اللهم إني أعوذ بك من جهد البلاء، ودرك الشقاء، وسوء القضاء، وشماتة الأعداءِ",
            "اللهم إني أسألك الهدى، والتقى، والعفاف، والغنىِ",
            "اللهم اهدني وسددني، اللهم إني أسألك الهدى والسدادِ",
            "اللهم إني أعوذ بك من شر ما عملت، ومن شر ما لم أعملِ",
            "اللهم إني أعوذ بك من زوال نعمتك، وتحول عافيتك، وفجاءة نقمتك، وجميع سخطك",
            "اللهم رحمتك أرجو فلا تكلني إلى نفسي طرفة عين، وأصلح لي شأني كله، لا إله إلا أنت",
            "اللهم مصرف القلوب صرف قلوبنا على طاعتك",
            "اللهم أحسن عاقبتنا في الأمور كلها، وأجرنا من خزي الدنيا وعذاب الآخرة",
            "اللهم إني أعوذ بك من شر سمعي، ومن شر بصري، ومن شر لساني، ومن شر قلبي، ومن شر منيي",
            "اللهم إني أعوذ بك من البرص، والجنون، والجذام، ومن سيئ الأسقام",
            "اللهم إنك عفو كريم تحب العفو فاعف عني",
            "اللهم اجعل أوسع رزقك علي عند كبر سني، وانقطاع عمري",
            "اللهم اغفر لي ذنبي، ووسع لي في داري، وبارك لي في رزقي",
            "اللهم إني أسألك من فضلك ورحمتك، فإنه لا يملكها إلا أنت",
            "اللهم إني أعوذ بك من الفقر، والقلة، والذلة، وأعوذ بك من أن أظلم أو أظلم",
            "اللهم إني أعوذ بك من جار السوء في دار المقامة؛ فإن جار البادية يتحول",
            "اللهم إني أعوذ بك أن أشرك بك وأنا أعلم، وأستغفرك لما لا أعلم",
            "اللهم انفعني بما علمتني، وعلمني ما ينفعني، وزدني علماً",
            "اللهم إني أسألك علماً نافعاً، ورزقاً طيباً، وعملاً متقبلاً",
            "رب اغفر لي، وتب علي، إنك أنت التواب الغفور",
            "اللهم إني أعوذ بك من البخل، والجبن، وسوء العمر، وفتنة الصدر، وعذاب القبر",
            "اللهم رب جبرائيل، وميكائيل، ورب إسرافيل، أعوذ بك من حر النار ومن عذاب القبر",
            "اللهم ألهمني رشدي، وأعذني من شر نفسي",
            "اللهم إني أسألك علماً نافعاً، وأعوذ بك من علم لا ينفع",
            "اللهم جنبني منكرات الأخلاق، والأهواء، والأعمال، والأدواء",
            "اللهم قنعني بما رزقتني، وبارك لي فيه، واخلف عليّ كل غائبة لي بخير",
            "اللهم أعنا على ذكرك، وشكرك، وحسن عبادتك",
            "اللهم إني أعوذ بك من غلبة الدين، وغلبة العدو، وشماتة الأعداء",
            "اللهم اغفر لي، واهدني، وارزقني، وعافني، أعوذ بالله من ضيق المقام يوم القيامة",
            "اللهم متعني بسمعي، وبصري، واجعلهما الوارث مني، وانصرني على من يظلمني، وخذ منه بثأري",
            "اللهم إني أسألك عيشة نقية، وميتة سوية، ومرداً غير مخز ولا فاضح",
            "اللهم زدنا ولا تنقصنا، وأكرمنا ولا تهنا، وأعطنا ولا تحرمنا، وآثرنا ولا تؤثر علينا، وأرضنا وارض عنا",
            "اللهم أحسنت خَلقي فأحسن خُلُقي",
            "اللهم اكفنى بحلالك عن حرامك وأعننى بفضلك عمن سواك",
            "اللهم قنى عذابك يوم تبعث عبادك",
            "يا حى ياقيوم برحمتك استغيث",
            "اللهم انى اعوذ بك من شر ماعملت"
        )

        // Get the current index
        var i = share.getInt(SettingSystemString.ITEM_DATA, 0)
        if (i >= data.size) {
            i = 0
        }
        val content = data[i]
        // Update the counter
        editor.putInt(SettingSystemString.ITEM_DATA, i + 1).apply()
        return content
    }

    companion object {
        const val NOTIFICATION_ID = 8
    }
}