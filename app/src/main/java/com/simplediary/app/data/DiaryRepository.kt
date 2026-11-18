package com.simplediary.app.data

import android.net.Uri
import com.simplediary.app.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class DiaryRepository(
    private val dao: DiaryDao,
    private val mediaFileStore: MediaFileStore
) {
    fun observeEntries(): Flow<List<DiaryEntry>> = dao.observeAll()

    fun observeEntry(id: Long): Flow<DiaryEntry?> = dao.observeById(id)

    fun observeMedia(entryId: Long): Flow<List<DiaryMedia>> = dao.observeMedia(entryId)

    fun observeOnThisDay(mode: OnThisDayMode): Flow<List<DiaryEntry>> {
        val today = DateUtils.today()
        return dao.observeAllExceptToday(today.toEpochDay()).map { entries ->
            entries.filter { matchesOnThisDay(it, today, mode) }
                .sortedByDescending { it.entryDateEpochDay }
        }
    }

    suspend fun getEntry(id: Long): DiaryEntry? = dao.getById(id)

    suspend fun cleanupEmptyDrafts() {
        dao.getEmptyEntries().forEach { deleteEntry(it.id) }
    }

    suspend fun createDraft(): Long {
        val now = System.currentTimeMillis()
        val entry = DiaryEntry(
            body = "",
            moodColor = MoodColors.DEFAULT,
            createdAt = now,
            updatedAt = now,
            entryDateEpochDay = DateUtils.epochDayOf(now)
        )
        return dao.insert(entry)
    }

    /**
     * Saves if there is text or media; otherwise deletes the draft entry.
     * @return true if kept/saved, false if discarded
     */
    suspend fun finalizeEditor(entryId: Long, body: String, moodColor: Long): Boolean {
        val trimmed = body.trim()
        val media = dao.getMedia(entryId)
        val hasContent = trimmed.isNotEmpty() || media.isNotEmpty()
        if (!hasContent) {
            deleteEntry(entryId)
            return false
        }
        val existing = dao.getById(entryId) ?: return false
        dao.update(
            existing.copy(
                body = trimmed,
                moodColor = moodColor,
                updatedAt = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun updateMood(entryId: Long, moodColor: Long) {
        val existing = dao.getById(entryId) ?: return
        dao.update(existing.copy(moodColor = moodColor, updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteEntry(entryId: Long) {
        dao.getMedia(entryId).forEach { mediaFileStore.deleteFile(it.filePath) }
        dao.deleteById(entryId)
    }

    suspend fun addMediaFromUri(entryId: Long, uri: Uri, type: MediaType): DiaryMedia {
        val path = mediaFileStore.copyFromUri(uri, type)
        val current = dao.getMedia(entryId)
        val media = DiaryMedia(
            entryId = entryId,
            type = type,
            filePath = path,
            sortOrder = current.size
        )
        val id = dao.insertMedia(media)
        return media.copy(id = id)
    }

    suspend fun addMediaFromPath(entryId: Long, path: String, type: MediaType): DiaryMedia {
        val current = dao.getMedia(entryId)
        val media = DiaryMedia(
            entryId = entryId,
            type = type,
            filePath = path,
            sortOrder = current.size
        )
        val id = dao.insertMedia(media)
        return media.copy(id = id)
    }

    fun createCaptureFile(type: MediaType) = mediaFileStore.createCaptureFile(type)

    suspend fun removeMedia(mediaId: Long) {
        val media = dao.getMediaById(mediaId) ?: return
        mediaFileStore.deleteFile(media.filePath)
        dao.deleteMediaById(mediaId)
    }

    private fun matchesOnThisDay(
        entry: DiaryEntry,
        today: LocalDate,
        mode: OnThisDayMode
    ): Boolean {
        val date = DateUtils.localDateOfEpochDay(entry.entryDateEpochDay)
        if (date == today) return false
        return when (mode) {
            OnThisDayMode.SAME_DAY_EACH_YEAR ->
                date.month == today.month && date.dayOfMonth == today.dayOfMonth

            OnThisDayMode.SAME_DAY_EACH_MONTH ->
                date.dayOfMonth == today.dayOfMonth

            OnThisDayMode.SAME_WEEKDAY_EACH_WEEK ->
                date.dayOfWeek == today.dayOfWeek
        }
    }
}
