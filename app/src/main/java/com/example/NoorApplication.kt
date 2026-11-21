package com.example

import android.app.Application
import com.example.data.local.db.NoorDatabase
import com.example.data.local.prefs.UserPreferencesRepository
import com.example.data.remote.QuranApiService
import com.example.data.repository.NoorRepository
import com.example.notifications.NoorNotificationHelper

class NoorApplication : Application() {

    lateinit var repository: NoorRepository
        private set

    override fun onCreate() {
        super.onCreate()
        NoorNotificationHelper.createChannels(this)
        val db = NoorDatabase.getInstance(this)
        val prefs = UserPreferencesRepository(this)
        val api = QuranApiService.create()
        repository = NoorRepository(db.noorDao(), prefs, api)
    }
}
