package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.SproutRepository
import com.example.data.local.AppDatabase
import com.example.model.ECO_TIPS_POOL
import com.example.model.NotificationCategory
import com.example.model.PlantMood
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CareReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getInstance(context).sproutDao()
                val profile = dao.getProfileOnce() ?: return@launch
                if (!profile.remindersEnabled) return@launch

                val metrics = SproutRepository.computeHydrationMetrics(profile)
                val (title, message, category) = when (metrics.mood) {
                    PlantMood.CRITICAL -> Triple(
                        "¡${profile.plantName} necesita agua urgente!",
                        "La hidratación bajó al ${metrics.hydrationPercent.toInt()}%. ¡Abre SproutPal para darle de beber!",
                        NotificationCategory.WATERING
                    )
                    PlantMood.THIRSTY -> Triple(
                        "Hora de revisar a ${profile.plantName}",
                        "Su hidratación está al ${metrics.hydrationPercent.toInt()}%. ¡Un poco de agua la mantendrá feliz!",
                        NotificationCategory.WATERING
                    )
                    else -> {
                        val tip = ECO_TIPS_POOL.random()
                        Triple(tip.first, tip.second, tip.third)
                    }
                }

                NotificationHelper.postNotification(context, title, message)
                SproutRepository(dao).addNotificationLog(title, message, category)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
