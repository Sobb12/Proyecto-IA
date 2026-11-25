package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_profile")
data class PlantProfileEntity(
    @PrimaryKey val id: Int = 1,
    val plantName: String,
    val plantTypeName: String,
    val lastWateredTimestamp: Long,
    val virtualOffsetMillis: Long = 0L,
    val totalWaterings: Int = 0,
    val streakDays: Int = 1,
    val lastStreakDayIndex: Long = 0L,
    val unlockedFactsCount: Int = 2,
    val remindersEnabled: Boolean = true,
    val ecoTipsEnabled: Boolean = true,
    val lastAlertBracket: String = "HEALTHY"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val role: String, // "user" or "model"
    val text: String,
    val timestamp: Long
)

@Entity(tableName = "notification_logs")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val message: String,
    val category: String, // NotificationCategory.name
    val timestamp: Long,
    val isRead: Boolean = false
)
