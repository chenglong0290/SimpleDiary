package com.simplediary.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val body: String = "",
    val moodColor: Long = MoodColors.DEFAULT,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    /** Local calendar date as epoch day for on-this-day queries. */
    val entryDateEpochDay: Long = 0
)
