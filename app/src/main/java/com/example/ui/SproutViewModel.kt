package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.EcoTutorService
import com.example.data.SproutRepository
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.NotificationLogEntity
import com.example.data.local.PlantProfileEntity
import com.example.model.ALL_ECO_FACTS
import com.example.model.ECO_TIPS_POOL
import com.example.model.NotificationCategory
import com.example.model.PlantMood
import com.example.model.PlantType
import com.example.notifications.NotificationHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "SproutViewModel"

sealed interface ChatUiState {
    data object Idle : ChatUiState
    data object Loading : ChatUiState
    data class Streaming(val text: String) : ChatUiState
    data class Error(val message: String) : ChatUiState
}

data class SimulatedBanner(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val category: NotificationCategory
)

data class SproutUiState(
    val isReady: Boolean = false,
    val plantName: String = "Sprout",
    val plantType: PlantType = PlantType.SUCCULENT,
    val hydrationPercent: Float = 90f,
    val remainingHours: Int = 216,
    val elapsedHours: Int = 24,
    val mood: PlantMood = PlantMood.HEALTHY,
    val totalWaterings: Int = 1,
    val streakDays: Int = 1,
    val unlockedFactsCount: Int = 2,
    val totalFactsCount: Int = ALL_ECO_FACTS.size,
    val virtualOffsetHours: Int = 0,
    val remindersEnabled: Boolean = true,
    val ecoTipsEnabled: Boolean = true,
    val chatMessages: List<ChatMessageEntity> = emptyList(),
    val chatUiState: ChatUiState = ChatUiState.Idle,
    val notifications: List<NotificationLogEntity> = emptyList(),
    val unreadNotificationsCount: Int = 0,
    val activeBanner: SimulatedBanner? = null,
    val newlyUnlockedFactMessage: String? = null
)

class SproutViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).sproutDao()
    private val repository = SproutRepository(dao)
    private val ecoTutorService = EcoTutorService()

    private val _tickerMillis = MutableStateFlow(System.currentTimeMillis())
    private val _isGrowing = MutableStateFlow(false)
    private val _chatUiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    private val _activeBanner = MutableStateFlow<SimulatedBanner?>(null)
    private val _newlyUnlockedFactMessage = MutableStateFlow<String?>(null)

    private var growingJob: Job? = null
    private var bannerAutoDismissJob: Job? = null
    private var chatJob: Job? = null

    private data class TransientUiState(
        val nowMillis: Long,
        val isGrowing: Boolean,
        val chatUiState: ChatUiState,
        val activeBanner: SimulatedBanner?,
        val unlockedMessage: String?
    )

    private val transientFlow = combine(
        _tickerMillis,
        _isGrowing,
        _chatUiState,
        _activeBanner,
        _newlyUnlockedFactMessage
    ) { nowMillis, isGrowing, chatUiState, activeBanner, unlockedMsg ->
        TransientUiState(
            nowMillis = nowMillis,
            isGrowing = isGrowing,
            chatUiState = chatUiState,
            activeBanner = activeBanner,
            unlockedMessage = unlockedMsg
        )
    }

    val uiState: StateFlow<SproutUiState> = combine(
        repository.profileFlow,
        repository.chatMessagesFlow,
        repository.notificationsFlow,
        transientFlow
    ) { profile, chatMessages, notifications, transient ->
        if (profile == null) {
            SproutUiState(isReady = false)
        } else {
            val plantType = PlantType.fromName(profile.plantTypeName)
            val metrics = SproutRepository.computeHydrationMetrics(
                profile = profile,
                nowMillis = transient.nowMillis,
                isGrowingAnimation = transient.isGrowing
            )
            checkThresholdAlert(profile, metrics.hydrationPercent)

            SproutUiState(
                isReady = true,
                plantName = profile.plantName,
                plantType = plantType,
                hydrationPercent = metrics.hydrationPercent,
                remainingHours = metrics.remainingHours,
                elapsedHours = metrics.elapsedHours,
                mood = metrics.mood,
                totalWaterings = profile.totalWaterings,
                streakDays = profile.streakDays,
                unlockedFactsCount = profile.unlockedFactsCount,
                totalFactsCount = ALL_ECO_FACTS.size,
                virtualOffsetHours = (profile.virtualOffsetMillis / 3_600_000L).toInt(),
                remindersEnabled = profile.remindersEnabled,
                ecoTipsEnabled = profile.ecoTipsEnabled,
                chatMessages = chatMessages,
                chatUiState = transient.chatUiState,
                notifications = notifications,
                unreadNotificationsCount = notifications.count { !it.isRead },
                activeBanner = transient.activeBanner,
                newlyUnlockedFactMessage = transient.unlockedMessage
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SproutUiState(isReady = false)
    )

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
            val ctx = getApplication<Application>()
            NotificationHelper.createNotificationChannel(ctx)
            NotificationHelper.schedulePeriodicReminders(ctx, enabled = true)
        }
        viewModelScope.launch {
            while (true) {
                delay(1_000L)
                _tickerMillis.value = System.currentTimeMillis()
            }
        }
    }

    private fun checkThresholdAlert(profile: PlantProfileEntity, hydrationPercent: Float) {
        if (!profile.remindersEnabled) return
        val newBracket = when {
            hydrationPercent < 30f -> "CRITICAL"
            hydrationPercent <= 60f -> "THIRSTY"
            else -> "HEALTHY"
        }
        if (newBracket != profile.lastAlertBracket && newBracket != "HEALTHY") {
            viewModelScope.launch {
                repository.updateLastAlertBracket(newBracket)
                val (title, message) = if (newBracket == "CRITICAL") {
                    "¡Urgente: ${profile.plantName} se está marchitando!" to
                        "La hidratación bajó al ${hydrationPercent.toInt()}%. Riega a ${profile.plantName} de inmediato para recuperarla."
                } else {
                    "${profile.plantName} tiene sed" to
                        "Su hidratación está al ${hydrationPercent.toInt()}%. ¡Planifica un riego pronto!"
                }
                dispatchNotification(title, message, NotificationCategory.WATERING)
            }
        } else if (newBracket == "HEALTHY" && profile.lastAlertBracket != "HEALTHY") {
            viewModelScope.launch {
                repository.updateLastAlertBracket("HEALTHY")
            }
        }
    }

    fun selectPlantType(plantType: PlantType) {
        viewModelScope.launch {
            repository.selectPlantType(plantType)
            _tickerMillis.value = System.currentTimeMillis()
        }
    }

    fun renamePlant(newName: String) {
        viewModelScope.launch {
            repository.renamePlant(newName)
        }
    }

    fun waterPlant() {
        viewModelScope.launch {
            val outcome = repository.waterPlant()
            _tickerMillis.value = System.currentTimeMillis()
            if (outcome.didWater) {
                triggerGrowingAnimation()
                if (outcome.newlyUnlockedFactTitle != null) {
                    _newlyUnlockedFactMessage.value =
                        "Dato Eco desbloqueado: ${outcome.newlyUnlockedFactTitle}"
                    delay(4_500L)
                    _newlyUnlockedFactMessage.value = null
                }
            }
        }
    }

    private fun triggerGrowingAnimation() {
        growingJob?.cancel()
        growingJob = viewModelScope.launch {
            _isGrowing.value = true
            delay(3_600L)
            _isGrowing.value = false
        }
    }

    fun fastForwardHours(hours: Int) {
        viewModelScope.launch {
            repository.fastForwardHours(hours)
            _tickerMillis.value = System.currentTimeMillis()
        }
    }

    fun jumpToHydration(targetPercent: Float) {
        viewModelScope.launch {
            _isGrowing.value = false
            repository.setHydrationForDemo(targetPercent)
            _tickerMillis.value = System.currentTimeMillis()
        }
    }

    fun resetDemo() {
        viewModelScope.launch {
            _isGrowing.value = false
            repository.resetDemoState()
            _tickerMillis.value = System.currentTimeMillis()
        }
    }

    fun triggerSimulatedNotification(category: NotificationCategory) {
        val state = uiState.value
        viewModelScope.launch {
            val (title, message) = when (category) {
                NotificationCategory.WATERING -> {
                    when {
                        state.hydrationPercent < 30f ->
                            "¡Urgente: ${state.plantName} necesita agua!" to
                                "La tierra está muy seca (${state.hydrationPercent.toInt()}%). ¡Toca Regar Planta ahora!"
                        state.hydrationPercent <= 60f ->
                            "${state.plantName} quiere un sorbo" to
                                "La hidratación está al ${state.hydrationPercent.toInt()}%. Mantener la humedad ayuda a sus raíces."
                        else ->
                            "Revisión de Cuidado: ${state.plantName}" to
                                "¡${state.plantName} está feliz al ${state.hydrationPercent.toInt()}%! Próximo riego en ${formatDuration(state.remainingHours)}."
                    }
                }
                NotificationCategory.SDG11 -> {
                    val tip = ECO_TIPS_POOL.filter { it.third == NotificationCategory.SDG11 }.random()
                    tip.first to tip.second
                }
                NotificationCategory.SDG15 -> {
                    val tip = ECO_TIPS_POOL.filter { it.third == NotificationCategory.SDG15 }.random()
                    tip.first to tip.second
                }
            }
            dispatchNotification(title, message, category)
        }
    }

    private suspend fun dispatchNotification(
        title: String,
        message: String,
        category: NotificationCategory
    ) {
        val ctx = getApplication<Application>()
        NotificationHelper.postNotification(ctx, title, message)
        repository.addNotificationLog(title, message, category)
        showInAppBanner(title, message, category)
    }

    private fun showInAppBanner(
        title: String,
        message: String,
        category: NotificationCategory
    ) {
        _activeBanner.value = SimulatedBanner(
            title = title,
            message = message,
            category = category
        )
        bannerAutoDismissJob?.cancel()
        bannerAutoDismissJob = viewModelScope.launch {
            delay(6_000L)
            _activeBanner.value = null
        }
    }

    fun dismissBanner() {
        bannerAutoDismissJob?.cancel()
        _activeBanner.value = null
    }

    fun dismissUnlockedFactBanner() {
        _newlyUnlockedFactMessage.value = null
    }

    fun toggleReminders(enabled: Boolean) {
        viewModelScope.launch {
            repository.setRemindersEnabled(enabled)
            NotificationHelper.schedulePeriodicReminders(getApplication(), enabled)
        }
    }

    fun toggleEcoTips(enabled: Boolean) {
        viewModelScope.launch {
            repository.setEcoTipsEnabled(enabled)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun sendChatMessage(rawPrompt: String) {
        val prompt = rawPrompt.trim()
        if (prompt.isEmpty() || _chatUiState.value is ChatUiState.Loading || _chatUiState.value is ChatUiState.Streaming) {
            return
        }

        val snapshot = uiState.value
        chatJob?.cancel()
        chatJob = viewModelScope.launch {
            _chatUiState.value = ChatUiState.Loading
            repository.addChatMessage("user", prompt)
            val historySnapshot = repository.getChatMessagesOnce()

            var latestText = ""
            try {
                ecoTutorService.streamPlantResponse(
                    plantName = snapshot.plantName,
                    plantType = snapshot.plantType,
                    hydrationPercent = snapshot.hydrationPercent,
                    mood = snapshot.mood,
                    history = historySnapshot,
                    userPrompt = prompt
                ).collect { partial ->
                    latestText = partial
                    _chatUiState.value = ChatUiState.Streaming(partial)
                }

                if (latestText.isNotBlank()) {
                    repository.addChatMessage("model", latestText)
                    _chatUiState.value = ChatUiState.Idle
                } else {
                    _chatUiState.value = ChatUiState.Error("No se recibió respuesta del Eco-Tutor.")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Eco-Tutor chat error", e)
                _chatUiState.value = ChatUiState.Error(
                    e.localizedMessage ?: "No se pudo conectar con el Eco-Tutor ahora. Revisa tu conexión e inténtalo de nuevo."
                )
            }
        }
    }

    fun dismissChatError() {
        if (_chatUiState.value is ChatUiState.Error) {
            _chatUiState.value = ChatUiState.Idle
        }
    }

    fun clearChatHistory() {
        val state = uiState.value
        viewModelScope.launch {
            chatJob?.cancel()
            _chatUiState.value = ChatUiState.Idle
            repository.clearChatHistory(state.plantName, state.plantType)
        }
    }

    companion object {
        fun formatDuration(hours: Int): String {
            if (hours <= 0) return "Toca ahora"
            val days = hours / 24
            val remHours = hours % 24
            return when {
                days > 0 && remHours > 0 -> "${days}d ${remHours}h"
                days > 0 -> "${days}d"
                else -> "${remHours}h"
            }
        }
    }
}
