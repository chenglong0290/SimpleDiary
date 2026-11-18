package com.simplediary.app

import android.app.Application
import com.simplediary.app.data.DiaryDatabase
import com.simplediary.app.data.DiaryRepository
import com.simplediary.app.data.MediaFileStore

class SimpleDiaryApp : Application() {
    lateinit var repository: DiaryRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = DiaryDatabase.get(this)
        repository = DiaryRepository(db.diaryDao(), MediaFileStore(this))
    }
}
