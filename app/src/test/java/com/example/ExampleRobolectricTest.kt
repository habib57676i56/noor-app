package com.example

import android.app.NotificationManager
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.db.NoorDatabase
import com.example.data.local.prefs.UserPreferences
import com.example.data.local.prefs.UserPreferencesRepository
import com.example.data.remote.QuranApiService
import com.example.data.repository.NoorRepository
import com.example.domain.model.AppLanguage
import com.example.notifications.NoorAlarmScheduler
import com.example.notifications.NoorNotificationHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `app name and notification channels initialize properly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("Noor", context.getString(R.string.app_name))

        NoorNotificationHelper.createChannels(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        assertNotNull(nm.getNotificationChannel(NoorNotificationHelper.CHANNEL_PRAYER))
        assertNotNull(nm.getNotificationChannel(NoorNotificationHelper.CHANNEL_REMINDER))

        // Verify alarm scheduling executes without throwing
        NoorAlarmScheduler.rescheduleAll(context, UserPreferences())
    }

    @Test
    fun `room database persists tasbih sessions bookmarks and json backup roundtrip`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, NoorDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val prefs = UserPreferencesRepository(context)
        val repo = NoorRepository(db.noorDao(), prefs, QuranApiService.create())

        repo.saveCompletedTasbihSession("subhanallah", "سبحان الله", "SubhanAllah", 33, 33)
        repo.toggleBookmark(1, 1, "الفاتحة", "Al-Fatihah", "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", false)
        repo.toggleFavoriteDhikr("m_1", false)

        val sessions = repo.tasbihSessions.first()
        val bookmarks = repo.quranBookmarks.first()
        assertEquals(1, sessions.size)
        assertEquals(33, sessions.first().count)
        assertEquals(1, bookmarks.size)

        val json = repo.exportBackupJson()
        assertTrue(json.contains("\"app\": \"NOOR\""))

        repo.resetAllAppData()
        assertTrue(repo.tasbihSessions.first().isEmpty())

        val restored = repo.importBackupJson(json)
        assertTrue(restored)
        assertEquals(1, repo.tasbihSessions.first().size)
        assertEquals(1, repo.quranBookmarks.first().size)

        db.close()
    }

    @Test
    fun `navigation across main screens and tasbih counter work interactively`() {
        composeRule.waitForIdle()

        // Home screen renders hero countdown
        composeRule.onNodeWithTag("home_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("hero_next_prayer_card").assertIsDisplayed()

        // Navigate to Prayer Times
        composeRule.onNodeWithTag("nav_prayers").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("prayer_times_screen").assertIsDisplayed()

        // Navigate to Quran
        composeRule.onNodeWithTag("nav_quran").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("quran_screen").assertIsDisplayed()

        // Navigate to Adhkar
        composeRule.onNodeWithTag("nav_adhkar").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("adhkar_screen").assertIsDisplayed()

        // Navigate to Tasbih & test live counter taps
        composeRule.onNodeWithTag("nav_tasbih").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("tasbih_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("tasbih_tap_button").performClick()
        composeRule.onNodeWithTag("tasbih_tap_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("tasbih_count_text", useUnmergedTree = true).assertTextEquals("2")

        // Verify Arabic RTL is default
        assertTrue(AppLanguage.ARABIC.isRtl)
    }
}
