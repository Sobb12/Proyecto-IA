package com.example.data

import com.example.data.local.ChatMessageEntity
import com.example.data.local.NotificationLogEntity
import com.example.data.local.PlantProfileEntity
import com.example.data.local.SproutDao
import com.example.model.ALL_ECO_FACTS
import com.example.model.NotificationCategory
import com.example.model.PlantMood
import com.example.model.PlantType
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class SproutRepository(private val dao: SproutDao) {

    val profileFlow: Flow<PlantProfileEntity?> = dao.observeProfile()
    val chatMessagesFlow: Flow<List<ChatMessageEntity>> = dao.observeChatMessages()
    val notificationsFlow: Flow<List<NotificationLogEntity>> = dao.observeNotifications()

    suspend fun ensureInitialized() {
        val existing = dao.getProfileOnce()
        if (existing == null) {
            val now = System.currentTimeMillis()
            val initialWatered = now - TimeUnit.HOURS.toMillis(24) // Start at ~90% healthy
            val dayIndex = TimeUnit.MILLISECONDS.toDays(now)
            dao.upsertProfile(
                PlantProfileEntity(
                    id = 1,
                    plantName = "Sprout",
                    plantTypeName = PlantType.SUCCULENT.name,
                    lastWateredTimestamp = initialWatered,
                    virtualOffsetMillis = 0L,
                    totalWaterings = 1,
                    streakDays = 1,
                    lastStreakDayIndex = dayIndex,
                    unlockedFactsCount = 2,
                    remindersEnabled = true,
                    ecoTipsEnabled = true,
                    lastAlertBracket = "HEALTHY"
                )
            )
            dao.insertChatMessage(
                ChatMessageEntity(
                    role = "model",
                    text = "¡Hola! Soy Sprout, tu Suculenta Jade. Pregúntame cómo cuidarme, o descubre cómo las plantas ayudan a las ciudades sostenibles (ODS 11) y la vida en la tierra (ODS 15).",
                    timestamp = now
                )
            )
            dao.insertNotification(
                NotificationLogEntity(
                    title = "¡Bienvenido a SproutPal!",
                    message = "Tu planta virtual está lista. Riégala a tiempo para mantener su hidratación sobre el 60% y desbloquear datos de los ODS.",
                    category = NotificationCategory.SDG15.name,
                    timestamp = now,
                    isRead = false
                )
            )
        }
    }

    suspend fun selectPlantType(plantType: PlantType) {
        val current = dao.getProfileOnce() ?: return
        val effectiveNow = System.currentTimeMillis() + current.virtualOffsetMillis
        dao.upsertProfile(
            current.copy(
                plantTypeName = plantType.name,
                lastWateredTimestamp = effectiveNow - TimeUnit.HOURS.toMillis(6),
                lastAlertBracket = "HEALTHY"
            )
        )
    }

    suspend fun renamePlant(newName: String) {
        val trimmed = newName.trim().take(20)
        if (trimmed.isEmpty()) return
        val current = dao.getProfileOnce() ?: return
        dao.upsertProfile(current.copy(plantName = trimmed))
    }

    suspend fun waterPlant(): WateringOutcome {
        val current = dao.getProfileOnce() ?: return WateringOutcome(false, null)
        val effectiveNow = System.currentTimeMillis() + current.virtualOffsetMillis
        val currentDayIndex = TimeUnit.MILLISECONDS.toDays(effectiveNow)
        val newStreak = when {
            currentDayIndex <= current.lastStreakDayIndex -> current.streakDays.coerceAtLeast(1)
            currentDayIndex == current.lastStreakDayIndex + 1 -> current.streakDays + 1
            else -> 1
        }
        val nextUnlocked = (current.unlockedFactsCount + 1).coerceAtMost(ALL_ECO_FACTS.size)
        val unlockedNewFact = if (nextUnlocked > current.unlockedFactsCount) {
            ALL_ECO_FACTS.getOrNull(nextUnlocked - 1)?.title
        } else null

        dao.upsertProfile(
            current.copy(
                lastWateredTimestamp = effectiveNow,
                totalWaterings = current.totalWaterings + 1,
                streakDays = newStreak,
                lastStreakDayIndex = currentDayIndex,
                unlockedFactsCount = nextUnlocked,
                lastAlertBracket = "HEALTHY"
            )
        )
        dao.insertNotification(
            NotificationLogEntity(
                title = "¡Recién Regada!",
                message = buildString {
                    append("¡${current.plantName} brilla al 100% de hidratación!")
                    if (unlockedNewFact != null) {
                        append(" Dato eco desbloqueado: $unlockedNewFact.")
                    }
                },
                category = NotificationCategory.WATERING.name,
                timestamp = System.currentTimeMillis(),
                isRead = true
            )
        )
        return WateringOutcome(didWater = true, newlyUnlockedFactTitle = unlockedNewFact)
    }

    suspend fun fastForwardHours(hours: Int) {
        val current = dao.getProfileOnce() ?: return
        val deltaMillis = TimeUnit.HOURS.toMillis(hours.toLong())
        dao.upsertProfile(
            current.copy(virtualOffsetMillis = current.virtualOffsetMillis + deltaMillis)
        )
    }

    suspend fun setHydrationForDemo(targetPercent: Float) {
        val current = dao.getProfileOnce() ?: return
        val plantType = PlantType.fromName(current.plantTypeName)
        val intervalMillis = TimeUnit.HOURS.toMillis(plantType.wateringIntervalHours.toLong())
        val elapsedNeeded = ((1f - (targetPercent / 100f).coerceIn(0f, 1f)) * intervalMillis).toLong()
        val effectiveNow = System.currentTimeMillis() + current.virtualOffsetMillis
        dao.upsertProfile(
            current.copy(lastWateredTimestamp = effectiveNow - elapsedNeeded)
        )
    }

    suspend fun resetDemoState() {
        val now = System.currentTimeMillis()
        val current = dao.getProfileOnce()
        val plantType = PlantType.fromName(current?.plantTypeName ?: PlantType.SUCCULENT.name)
        val plantName = current?.plantName ?: "Sprout"
        dao.upsertProfile(
            PlantProfileEntity(
                id = 1,
                plantName = plantName,
                plantTypeName = plantType.name,
                lastWateredTimestamp = now - TimeUnit.HOURS.toMillis(12),
                virtualOffsetMillis = 0L,
                totalWaterings = 1,
                streakDays = 1,
                lastStreakDayIndex = TimeUnit.MILLISECONDS.toDays(now),
                unlockedFactsCount = 2,
                remindersEnabled = current?.remindersEnabled ?: true,
                ecoTipsEnabled = current?.ecoTipsEnabled ?: true,
                lastAlertBracket = "HEALTHY"
            )
        )
    }

    suspend fun updateLastAlertBracket(bracket: String) {
        val current = dao.getProfileOnce() ?: return
        if (current.lastAlertBracket != bracket) {
            dao.upsertProfile(current.copy(lastAlertBracket = bracket))
        }
    }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        val current = dao.getProfileOnce() ?: return
        dao.upsertProfile(current.copy(remindersEnabled = enabled))
    }

    suspend fun setEcoTipsEnabled(enabled: Boolean) {
        val current = dao.getProfileOnce() ?: return
        dao.upsertProfile(current.copy(ecoTipsEnabled = enabled))
    }

    suspend fun addChatMessage(role: String, text: String) {
        dao.insertChatMessage(
            ChatMessageEntity(
                role = role,
                text = text,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun getChatMessagesOnce(): List<ChatMessageEntity> = dao.getChatMessagesOnce()

    suspend fun clearChatHistory(plantName: String, plantType: PlantType) {
        dao.clearChatMessages()
        dao.insertChatMessage(
            ChatMessageEntity(
                role = "model",
                text = "¡Empecemos de nuevo! Soy $plantName, tu ${plantType.displayName}. ¿Qué te gustaría aprender hoy sobre plantas, ciudades sostenibles (ODS 11) o vida terrestre (ODS 15)?",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun addNotificationLog(title: String, message: String, category: NotificationCategory) {
        dao.insertNotification(
            NotificationLogEntity(
                title = title,
                message = message,
                category = category.name,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
        )
    }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    companion object {
        fun computeHydrationMetrics(
            profile: PlantProfileEntity,
            nowMillis: Long = System.currentTimeMillis(),
            isGrowingAnimation: Boolean = false
        ): HydrationMetrics {
            val plantType = PlantType.fromName(profile.plantTypeName)
            val effectiveNow = nowMillis + profile.virtualOffsetMillis
            val intervalMillis = TimeUnit.HOURS.toMillis(plantType.wateringIntervalHours.toLong())
            val elapsedMillis = (effectiveNow - profile.lastWateredTimestamp).coerceAtLeast(0L)
            val hydration = ((1.0 - elapsedMillis.toDouble() / intervalMillis.toDouble()) * 100.0)
                .coerceIn(0.0, 100.0)
                .toFloat()
            val remainingMillis = (intervalMillis - elapsedMillis).coerceAtLeast(0L)
            val remainingHours = (remainingMillis / 3_600_000L).toInt()

            val mood = when {
                isGrowingAnimation -> PlantMood.GROWING
                hydration > 60f -> PlantMood.HEALTHY
                hydration >= 30f -> PlantMood.THIRSTY
                else -> PlantMood.CRITICAL
            }
            return HydrationMetrics(
                hydrationPercent = hydration,
                remainingHours = remainingHours,
                elapsedHours = (elapsedMillis / 3_600_000L).toInt(),
                mood = mood,
                effectiveNowMillis = effectiveNow
            )
        }
    }
}

data class HydrationMetrics(
    val hydrationPercent: Float,
    val remainingHours: Int,
    val elapsedHours: Int,
    val mood: PlantMood,
    val effectiveNowMillis: Long
)

data class WateringOutcome(
    val didWater: Boolean,
    val newlyUnlockedFactTitle: String?
)
