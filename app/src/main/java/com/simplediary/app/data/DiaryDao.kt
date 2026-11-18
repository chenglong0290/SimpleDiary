package com.simplediary.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entries ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getById(id: Long): DiaryEntry?

    @Query("SELECT * FROM diary_entries WHERE id = :id")
    fun observeById(id: Long): Flow<DiaryEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DiaryEntry): Long

    @Update
    suspend fun update(entry: DiaryEntry)

    @Delete
    suspend fun delete(entry: DiaryEntry)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        SELECT e.* FROM diary_entries e
        WHERE TRIM(e.body) = ''
        AND NOT EXISTS (SELECT 1 FROM diary_media m WHERE m.entryId = e.id)
        """
    )
    suspend fun getEmptyEntries(): List<DiaryEntry>

    @Query(
        """
        SELECT * FROM diary_entries
        WHERE entryDateEpochDay != :todayEpochDay
        ORDER BY createdAt DESC
        """
    )
    fun observeAllExceptToday(todayEpochDay: Long): Flow<List<DiaryEntry>>

    @Insert
    suspend fun insertMedia(media: DiaryMedia): Long

    @Query("SELECT * FROM diary_media WHERE entryId = :entryId ORDER BY sortOrder ASC, createdAt ASC")
    fun observeMedia(entryId: Long): Flow<List<DiaryMedia>>

    @Query("SELECT * FROM diary_media WHERE entryId = :entryId ORDER BY sortOrder ASC, createdAt ASC")
    suspend fun getMedia(entryId: Long): List<DiaryMedia>

    @Query("DELETE FROM diary_media WHERE id = :mediaId")
    suspend fun deleteMediaById(mediaId: Long)

    @Query("SELECT * FROM diary_media WHERE id = :mediaId")
    suspend fun getMediaById(mediaId: Long): DiaryMedia?
}
