package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.FocusSession
import com.example.data.StatsRepository
import com.example.data.StoreItem
import com.example.data.UserStats
import com.example.service.FocusTimerService
import com.example.util.DeviceHealthTracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UiEvent {
    data class ShowToast(val msg: String) : UiEvent()
    object StrictModeBlocked : UiEvent()
}

class SharedStatsViewModel(
    private val repository: StatsRepository,
    private val appContext: Context
) : ViewModel() {

    val stats: StateFlow<UserStats> = repository.stats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserStats()
    )

    val storeItems: StateFlow<List<StoreItem>> = repository.storeItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentSessions: StateFlow<List<FocusSession>> = repository.recentSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _timerRunning = MutableStateFlow(false)
    val timerRunning: StateFlow<Boolean> = _timerRunning

    private val _focusMinutes = MutableStateFlow(25)
    val focusMinutes: StateFlow<Int> = _focusMinutes

    private val _remainingSeconds = MutableStateFlow(25 * 60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds

    private val _selectedMode = MutableStateFlow("normal")
    val selectedMode: StateFlow<String> = _selectedMode

    private val _hasUsagePermission = MutableStateFlow(false)
    val hasUsagePermission: StateFlow<Boolean> = _hasUsagePermission

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initDatabaseIfNeeded()
            checkAndSyncHealthData()
        }
    }

    fun checkAndSyncHealthData() {
        val hasPerm = DeviceHealthTracker.hasUsagePermission(appContext)
        _hasUsagePermission.value = hasPerm
        if (hasPerm) {
            val screenHours = DeviceHealthTracker.getTodayScreenTimeHours(appContext)
            val sleepHours = DeviceHealthTracker.getEstimatedSleepHours(appContext)
            viewModelScope.launch {
                repository.updateDeviceHealth(screenHours, sleepHours)
            }
        }
    }

    fun openUsageSettings() {
        DeviceHealthTracker.openUsageAccessSettings(appContext)
    }

    fun startTimer() {
        if (_timerRunning.value) return
        _timerRunning.value = true

        val isInfinite = _selectedMode.value == "infinite"
        FocusTimerService.startService(appContext, _remainingSeconds.value, isInfinite)

        timerJob = viewModelScope.launch {
            if (isInfinite) {
                while (true) {
                    delay(1000)
                    _remainingSeconds.value += 1
                    if (_remainingSeconds.value % 5 == 0) {
                        FocusTimerService.updateNotification(appContext, _remainingSeconds.value, true)
                    }
                }
            } else {
                while (_remainingSeconds.value > 0) {
                    delay(1000)
                    _remainingSeconds.value -= 1
                    if (_remainingSeconds.value % 5 == 0) {
                        FocusTimerService.updateNotification(appContext, _remainingSeconds.value, false)
                    }
                }
                // Timer finished normally
                _timerRunning.value = false
                FocusTimerService.stopService(appContext)

                val coins = repository.completeFocusSession(_focusMinutes.value, _selectedMode.value)
                _uiEvent.emit(UiEvent.ShowToast("🎉 Hoàn thành! +$coins MoonCoins"))
                _remainingSeconds.value = _focusMinutes.value * 60
            }
        }
    }

    fun pauseTimer() {
        if (_selectedMode.value == "strict") {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.StrictModeBlocked)
            }
            return
        }
        _timerRunning.value = false
        timerJob?.cancel()
        FocusTimerService.stopService(appContext)

        if (_selectedMode.value == "infinite") {
            val elapsedMinutes = _remainingSeconds.value / 60
            if (elapsedMinutes > 0) {
                viewModelScope.launch {
                    val coins = repository.completeFocusSession(elapsedMinutes, _selectedMode.value)
                    _uiEvent.emit(UiEvent.ShowToast("🎉 Hoàn thành! +$coins MoonCoins"))
                }
            }
            _remainingSeconds.value = 0
        }
    }

    fun stopTimer() {
        if (_selectedMode.value == "strict") {
            viewModelScope.launch {
                _uiEvent.emit(UiEvent.StrictModeBlocked)
            }
            return
        }
        _timerRunning.value = false
        timerJob?.cancel()
        FocusTimerService.stopService(appContext)

        if (_selectedMode.value == "infinite") {
            val elapsedMinutes = _remainingSeconds.value / 60
            if (elapsedMinutes > 0) {
                viewModelScope.launch {
                    val coins = repository.completeFocusSession(elapsedMinutes, _selectedMode.value)
                    _uiEvent.emit(UiEvent.ShowToast("🎉 Hoàn thành! +$coins MoonCoins"))
                }
            }
            _remainingSeconds.value = 0
        } else {
            _remainingSeconds.value = _focusMinutes.value * 60
        }
    }

    fun setMode(mode: String) {
        if (!_timerRunning.value) {
            _selectedMode.value = mode
            if (mode == "infinite") {
                _remainingSeconds.value = 0
            } else {
                _remainingSeconds.value = _focusMinutes.value * 60
            }
        }
    }

    fun setFocusMinutes(minutes: Int) {
        if (!_timerRunning.value) {
            _focusMinutes.value = minutes
            if (_selectedMode.value != "infinite") {
                _remainingSeconds.value = minutes * 60
            }
        }
    }

    fun purchaseItem(itemId: String) {
        viewModelScope.launch {
            val result = repository.buyStoreItem(itemId)
            result.fold(
                onSuccess = { item ->
                    _uiEvent.emit(UiEvent.ShowToast("✨ Mua thành công: ${item.name}!"))
                },
                onFailure = { err ->
                    _uiEvent.emit(UiEvent.ShowToast(err.message ?: "Mua không thành công"))
                }
            )
        }
    }

    fun updateScreenTime(hours: Float) {
        viewModelScope.launch {
            repository.updateScreenTime(hours)
        }
    }

    fun updateSleep(hours: Float) {
        viewModelScope.launch {
            repository.updateSleep(hours)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        FocusTimerService.stopService(appContext)
    }
}

class SharedStatsViewModelFactory(
    private val repository: StatsRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedStatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedStatsViewModel(repository, context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
