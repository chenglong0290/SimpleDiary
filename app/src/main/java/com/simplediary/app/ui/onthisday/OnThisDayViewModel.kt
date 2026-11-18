package com.simplediary.app.ui.onthisday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simplediary.app.data.DiaryEntry
import com.simplediary.app.data.DiaryRepository
import com.simplediary.app.data.OnThisDayMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class OnThisDayViewModel(
    private val repository: DiaryRepository
) : ViewModel() {
    private val _mode = MutableStateFlow(OnThisDayMode.SAME_DAY_EACH_YEAR)
    val mode: StateFlow<OnThisDayMode> = _mode.asStateFlow()

    val entries: StateFlow<List<DiaryEntry>> = _mode
        .flatMapLatest { repository.observeOnThisDay(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setMode(mode: OnThisDayMode) {
        _mode.value = mode
    }

    fun cycleMode() {
        val values = OnThisDayMode.entries
        val next = values[(_mode.value.ordinal + 1) % values.size]
        _mode.value = next
    }

    fun updateMood(entryId: Long, color: Long) {
        viewModelScope.launch {
            repository.updateMood(entryId, color)
        }
    }

    class Factory(private val repository: DiaryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OnThisDayViewModel(repository) as T
        }
    }
}
