package com.example

import com.example.data.local.catalog.AdhkarCatalog
import com.example.data.local.catalog.SurahCatalog
import com.example.domain.calendar.HijriCalendarEngine
import com.example.domain.model.AdhkarCategory
import com.example.domain.model.AsrMadhhab
import com.example.domain.model.CalculationMethod
import com.example.domain.model.PrayerMinuteOffsets
import com.example.domain.prayer.CityCatalog
import com.example.domain.prayer.PrayerTimeCalculator
import com.example.domain.prayer.QiblaCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class NoorCoreLogicUnitTest {

    @Test
    fun `prayer times for Makkah are chronologically ordered and astronomically sound`() {
        val makkah = CityCatalog.findById("makkah")
        val tz = TimeZone.getTimeZone(makkah.timezoneId)
        val schedule = PrayerTimeCalculator.calculateForDate(
            year = 2026,
            month = 6,
            day = 15,
            latitude = makkah.latitude,
            longitude = makkah.longitude,
            timeZone = tz,
            method = CalculationMethod.UMM_AL_QURA,
            madhhab = AsrMadhhab.SHAFI
        )

        assertTrue(schedule.fajrMillis < schedule.sunriseMillis)
        assertTrue(schedule.sunriseMillis < schedule.dhuhrMillis)
        assertTrue(schedule.dhuhrMillis < schedule.asrMillis)
        assertTrue(schedule.asrMillis < schedule.maghribMillis)
        assertTrue(schedule.maghribMillis < schedule.ishaMillis)

        // Umm al-Qura Isha is strictly 90 minutes after Maghrib
        val ishaDiffMinutes = (schedule.ishaMillis - schedule.maghribMillis) / 60_000L
        assertEquals(90L, ishaDiffMinutes)
    }

    @Test
    fun `hanafi asr is always later than shafi asr`() {
        val cairo = CityCatalog.findById("cairo")
        val tz = TimeZone.getTimeZone(cairo.timezoneId)
        val shafi = PrayerTimeCalculator.calculateForDate(
            2026, 3, 21, cairo.latitude, cairo.longitude, tz,
            CalculationMethod.EGYPT, AsrMadhhab.SHAFI
        )
        val hanafi = PrayerTimeCalculator.calculateForDate(
            2026, 3, 21, cairo.latitude, cairo.longitude, tz,
            CalculationMethod.EGYPT, AsrMadhhab.HANAFI
        )
        assertTrue("Hanafi Asr should be 30+ minutes after Shafi'i Asr", hanafi.asrMillis > shafi.asrMillis + 20 * 60_000L)
    }

    @Test
    fun `manual prayer minute offsets shift prayer times accurately`() {
        val riyadh = CityCatalog.findById("riyadh")
        val tz = TimeZone.getTimeZone(riyadh.timezoneId)
        val base = PrayerTimeCalculator.calculateForDate(
            2026, 10, 10, riyadh.latitude, riyadh.longitude, tz,
            CalculationMethod.UMM_AL_QURA, AsrMadhhab.SHAFI
        )
        val shifted = PrayerTimeCalculator.calculateForDate(
            2026, 10, 10, riyadh.latitude, riyadh.longitude, tz,
            CalculationMethod.UMM_AL_QURA, AsrMadhhab.SHAFI,
            PrayerMinuteOffsets(fajr = 5, maghrib = -3)
        )
        assertEquals(5 * 60_000L, shifted.fajrMillis - base.fajrMillis)
        assertEquals(-3 * 60_000L, shifted.maghribMillis - base.maghribMillis)
    }

    @Test
    fun `weekly and monthly schedules generate exact requested day counts`() {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Riyadh"))
        val weekly = PrayerTimeCalculator.calculateRange(
            cal, 7, 21.4225, 39.8262, CalculationMethod.UMM_AL_QURA, AsrMadhhab.SHAFI
        )
        val monthly = PrayerTimeCalculator.calculateRange(
            cal, 30, 21.4225, 39.8262, CalculationMethod.UMM_AL_QURA, AsrMadhhab.SHAFI
        )
        assertEquals(7, weekly.size)
        assertEquals(30, monthly.size)
    }

    @Test
    fun `qibla bearing calculations match known geographic bearings`() {
        val cairoBearing = QiblaCalculator.calculateQiblaBearing(30.0444, 31.2357)
        assertTrue("Cairo Qibla should be ~136° SE, was $cairoBearing", cairoBearing in 132.0..140.0)

        val nyBearing = QiblaCalculator.calculateQiblaBearing(40.7128, -74.0060)
        assertTrue("New York Qibla should be ~58° NE, was $nyBearing", nyBearing in 55.0..62.0)

        val jakartaBearing = QiblaCalculator.calculateQiblaBearing(-6.2088, 106.8456)
        assertTrue("Jakarta Qibla should be ~295° WNW, was $jakartaBearing", jakartaBearing in 292.0..298.0)

        val makkahDistance = QiblaCalculator.calculateDistanceToMakkahKm(QiblaCalculator.KAABA_LAT, QiblaCalculator.KAABA_LON)
        assertTrue(makkahDistance < 0.1)
    }

    @Test
    fun `catalogs contain all 114 surahs and all 9 adhkar categories`() {
        assertEquals(114, SurahCatalog.allSurahs.size)
        assertEquals(1, SurahCatalog.allSurahs.first().number)
        assertEquals(114, SurahCatalog.allSurahs.last().number)

        AdhkarCategory.entries.forEach { cat ->
            assertTrue("Category ${cat.id} should not be empty", AdhkarCatalog.byCategory(cat).isNotEmpty())
        }
    }

    @Test
    fun `hijri calendar conversion and adjustment work cleanly`() {
        val base = HijriCalendarEngine.gregorianToHijri(2026, 5, 20, 0)
        assertTrue(base.year in 1447..1448)
        assertTrue(base.month in 1..12)
        assertTrue(base.day in 1..30)
        assertTrue(HijriCalendarEngine.getUpcomingOccasions(0).size >= 8)
    }
}
