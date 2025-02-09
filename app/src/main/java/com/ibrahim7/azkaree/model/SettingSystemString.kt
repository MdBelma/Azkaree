package com.ibrahim7.azkaree.model

class SettingSystemString {
    companion object {
        const val CHANNEL_ID = MyApp.CHANNEL_ID
        const val NOTIFY_DELETE = "com.example.azkaree.model.delete"
        const val NAME_TYPE_ATHKER = "nameTypeAthker"
        const val MEDIA = "media"
        const val NAME_FILE_SHER = "file"
        const val START_ACTIVITY = "start"
        const val COUNTER_NOTIFICATION = "counterNotification"
        const val NAME_ATHKER_FRAGMENT = "nameAthkerFragment"
        const val POISTION_ATHKER_FRAGMENT = "type"
        const val ITEM_DATA = "item"
        const val START_ALARM = "startAlarm"
    }
}

/**Here’s an example of how you can use these constants in your code:

Kotlin
import com.ibrahim7.azkaree.model.SettingSystemString

// Using the constants
val channelId = SettingSystemString.CHANNEL_ID
val notifyDeleteAction = SettingSystemString.NOTIFY_DELETE*/