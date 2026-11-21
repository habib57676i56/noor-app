package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.prefs.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoorAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(NoorAlarmScheduler.EXTRA_TITLE) ?: "نور · NOOR"
        val body = intent.getStringExtra(NoorAlarmScheduler.EXTRA_BODY) ?: ""
        val channel = intent.getStringExtra(NoorAlarmScheduler.EXTRA_CHANNEL)
            ?: NoorNotificationHelper.CHANNEL_PRAYER
        val id = intent.getIntExtra(NoorAlarmScheduler.EXTRA_NOTIFICATION_ID, 3001)

        NoorNotificationHelper.showNotification(context, id, channel, title, body)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = UserPreferencesRepository(context).getPreferencesOnce()
                NoorAlarmScheduler.rescheduleAll(context, prefs)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
