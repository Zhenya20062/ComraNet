package com.euzhene.comranet.service

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.euzhene.comranet.TAG_PRESENT
import com.onesignal.OSNotification
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler

@Keep
@Suppress("unused")
class NotificationChatDataService:OSRemoteNotificationReceivedHandler {
    override fun remoteNotificationReceived(p0: Context?, p1: OSNotificationReceivedEvent?) {
        val notification = p1?.notification
        p1?.complete(notification)
    }
}

