package com.simplediary.app.ui.diary

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simplediary.app.data.DiaryMedia
import com.simplediary.app.data.DiaryRepository
import com.simplediary.app.data.MediaType
import com.simplediary.app.data.MoodColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class DiaryEditorViewModel(
    private val entryId: Long,
    private val repository: DiaryRepository
) : ViewModel() {
    private val _body = MutableStateFlow("")
    val body: StateFlow<String> = _body.asStateFlow()

    private val _moodColor = MutableStateFlow(MoodColors.DEFAULT)
    val moodColor: StateFlow<Long> = _moodColor.asStateFlow()

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    val media: StateFlow<List<DiaryMedia>> = repository.observeMedia(entryId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            val entry = repository.getEntry(entryId)
            if (entry != null) {
                _body.value = entry.body
                _moodColor.value = entry.moodColor
            }
            _ready.value = true
        }
    }

    fun onBodyChange(value: String) {
        _body.value = value
    }

    fun onMoodChange(color: Long) {
        _moodColor.value = color
    }

    fun addMedia(uri: Uri, type: MediaType) {
        viewModelScope.launch {
            repository.addMediaFromUri(entryId, uri, type)
        }
    }

    fun addCapturedFile(file: File, type: MediaType) {
        viewModelScope.launch {
            repository.addMediaFromPath(entryId, file.absolutePath, type)
        }
    }

    fun createCaptureFile(type: MediaType): File = repository.createCaptureFile(type)

    fun removeMedia(mediaId: Long) {
        viewModelScope.launch {
            repository.removeMedia(mediaId)
        }
    }

    suspend fun finalizeAndExit(): Boolean {
        return repository.finalizeEditor(entryId, _body.value, _moodColor.value)
    }

    /** Permanently delete this diary and its media files. */
    suspend fun deleteDiary() {
        repository.deleteEntry(entryId)
    }

    class Factory(
        private val entryId: Long,
        private val repository: DiaryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DiaryEditorViewModel(entryId, repository) as T
        }
    }
}
