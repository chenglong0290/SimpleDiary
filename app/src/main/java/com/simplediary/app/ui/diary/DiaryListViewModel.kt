package com.simplediary.app.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simplediary.app.data.DiaryEntry
import com.simplediary.app.data.DiaryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiaryListViewModel(
    private val repository: DiaryRepository
) : ViewModel() {
    val entries: StateFlow<List<DiaryEntry>> = repository.observeEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            repository.cleanupEmptyDrafts()
        }
    }

    fun updateMood(entryId: Long, color: Long) {
        viewModelScope.launch {
            repository.updateMood(entryId, color)
        }
    }

    suspend fun createDraft(): Long = repository.createDraft()

    class Factory(private val repository: DiaryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DiaryListViewModel(repository) as T
        }
    }
}
