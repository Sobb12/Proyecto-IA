package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SproutDao {
    @Query("SELECT * FROM plant_profile WHERE id = 1")
    fun observeProfile(): Flow<PlantProfileEntity?>

    @Query("SELECT * FROM plant_profile WHERE id = 1")
    suspend fun getProfileOnce(): PlantProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: PlantProfileEntity)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC, id ASC")
    fun observeChatMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC, id ASC")
    suspend fun getChatMessagesOnce(): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()

    @Query("SELECT * FROM notification_logs ORDER BY timestamp DESC, id DESC")
    fun observeNotifications(): Flow<List<NotificationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationLogEntity)

    @Query("UPDATE notification_logs SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllNotificationsRead()

    @Query("DELETE FROM notification_logs")
    suspend fun clearNotifications()
}
