package com.example.domain.prayer

import com.example.domain.model.AsrMadhhab
import com.example.domain.model.CalculationMethod
import com.example.domain.model.DailyPrayerSchedule
import com.example.domain.model.NextPrayerInfo
import com.example.domain.model.PrayerMinuteOffsets
import com.example.domain.model.PrayerName
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

/**
 * Astronomical Islamic prayer time engine based on Jean Meeus solar ephemeris
 * algorithms (standard PrayTimes / Adhan methodology).
 * Runs 100% offline with high accuracy across global latitudes and calculation methods.
 */
object PrayerTimeCalculator {

    private const val SUNRISE_ALTITUDE_DEG = -0.8333

    fun calculateForDate(
        year: Int,
        month: Int, // 1..12
        day: Int,   // 1..31
        latitude: Double,
        longitude: Double,
        timeZone: TimeZone,
        method: CalculationMethod,
        madhhab: AsrMadhhab,
        offsets: PrayerMinuteOffsets = PrayerMinuteOffsets()
    ): DailyPrayerSchedule {
        val cal = Calendar.getInstance(timeZone).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val midnightMillis = cal.timeInMillis
        val tzOffsetHours = timeZone.getOffset(midnightMillis + 12 * 3_600_000L) / 3_600_000.0

        val jd = julianDate(year, month, day) - longitude / (15.0 * 24.0)

        // Initial approximations (in fraction of day)
        var fajr = 5.0 / 24.0
        var sunrise = 6.0 / 24.0
        var dhuhr = 12.0 / 24.0
        var asr = 15.0 / 24.0
        var maghrib = 18.0 / 24.0
        var isha = 19.5 / 24.0

        // Two-pass iterative refinement for solar declination & equation of time precision
        repeat(2) {
            dhuhr = computeMidDay(jd + dhuhr)
            sunrise = computeSunAngleTime(jd + sunrise, SUNRISE_ALTITUDE_DEG, latitude, ccw = true)
            maghrib = computeSunAngleTime(jd + maghrib, SUNRISE_ALTITUDE_DEG, latitude, ccw = false)

            val rawFajr = computeSunAngleTime(jd + fajr, -method.fajrAngle, latitude, ccw = true)
            val rawAsr = computeAsrTime(jd + asr, madhhab.shadowFactor, latitude)
            val rawIsha = if (method.ishaIntervalMinutes > 0) {
                maghrib + method.ishaIntervalMinutes / 60.0
            } else {
                computeSunAngleTime(jd + isha, -method.ishaAngle, latitude, ccw = false)
            }

            // High-latitude safety fallback (Angle-Based Rule) if astronomical twilight doesn't occur
            val nightDuration = normalizeHours(sunrise - maghrib)
            fajr = if (rawFajr.isNaN()) {
                sunrise - (method.fajrAngle / 60.0) * nightDuration
            } else {
                rawFajr
            }
            isha = if (rawIsha.isNaN()) {
                maghrib + (method.ishaAngle / 60.0) * nightDuration
            } else {
                rawIsha
            }
            asr = rawAsr

            // Convert back to day fractions for second pass
            fajr /= 24.0
            sunrise /= 24.0
            dhuhr /= 24.0
            asr /= 24.0
            maghrib /= 24.0
            isha /= 24.0
        }

        // Convert day fractions to local hours
        val tzAdjust = tzOffsetHours - longitude / 15.0
        val fajrHours = fajr * 24.0 + tzAdjust + offsets.fajr / 60.0
        val sunriseHours = sunrise * 24.0 + tzAdjust + offsets.sunrise / 60.0
        val dhuhrHours = dhuhr * 24.0 + tzAdjust + (1.0 / 60.0) + offsets.dhuhr / 60.0 // +1 min safety after zenith
        val asrHours = asr * 24.0 + tzAdjust + offsets.asr / 60.0
        val maghribHours = maghrib * 24.0 + tzAdjust + offsets.maghrib / 60.0
        val ishaHours = isha * 24.0 + tzAdjust + offsets.isha / 60.0

        val dateKey = String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
        val epochDay = midnightMillis / 86_400_000L

        return DailyPrayerSchedule(
            dateKey = dateKey,
            epochDay = epochDay,
            fajrMillis = hoursToMillis(midnightMillis, fajrHours),
            sunriseMillis = hoursToMillis(midnightMillis, sunriseHours),
            dhuhrMillis = hoursToMillis(midnightMillis, dhuhrHours),
            asrMillis = hoursToMillis(midnightMillis, asrHours),
            maghribMillis = hoursToMillis(midnightMillis, maghribHours),
            ishaMillis = hoursToMillis(midnightMillis, ishaHours)
        )
    }

    fun calculateForCalendar(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        method: CalculationMethod,
        madhhab: AsrMadhhab,
        offsets: PrayerMinuteOffsets = PrayerMinuteOffsets()
    ): DailyPrayerSchedule {
        return calculateForDate(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1,
            day = calendar.get(Calendar.DAY_OF_MONTH),
            latitude = latitude,
            longitude = longitude,
            timeZone = calendar.timeZone,
            method = method,
            madhhab = madhhab,
            offsets = offsets
        )
    }

    fun calculateRange(
        startCalendar: Calendar,
        days: Int,
        latitude: Double,
        longitude: Double,
        method: CalculationMethod,
        madhhab: AsrMadhhab,
        offsets: PrayerMinuteOffsets = PrayerMinuteOffsets()
    ): List<DailyPrayerSchedule> {
        val result = ArrayList<DailyPrayerSchedule>(days)
        val cursor = startCalendar.clone() as Calendar
        for (i in 0 until days) {
            result.add(calculateForCalendar(cursor, latitude, longitude, method, madhhab, offsets))
            cursor.add(Calendar.DAY_OF_MONTH, 1)
        }
        return result
    }

    fun computeNextPrayer(
        nowMillis: Long,
        todaySchedule: DailyPrayerSchedule,
        tomorrowSchedule: DailyPrayerSchedule
    ): NextPrayerInfo {
        val todayOrdered = todaySchedule.allOrdered()
        val nextIndex = todayOrdered.indexOfFirst { it.second > nowMillis }

        return if (nextIndex != -1) {
            val (nextPrayer, nextTime) = todayOrdered[nextIndex]
            val (prevPrayer, prevTime) = if (nextIndex > 0) {
                todayOrdered[nextIndex - 1]
            } else {
                // Before Fajr: previous prayer was yesterday's Isha (approx 8h before Fajr)
                PrayerName.ISHA to (todaySchedule.fajrMillis - 8 * 3_600_000L)
            }
            val span = (nextTime - prevTime).coerceAtLeast(1L)
            val elapsed = (nowMillis - prevTime).coerceIn(0L, span)
            NextPrayerInfo(
                nextPrayer = nextPrayer,
                nextPrayerMillis = nextTime,
                previousPrayer = prevPrayer,
                previousPrayerMillis = prevTime,
                remainingMillis = (nextTime - nowMillis).coerceAtLeast(0L),
                progress = elapsed.toFloat() / span.toFloat()
            )
        } else {
            // After today's Isha -> Next prayer is tomorrow's Fajr
            val prevTime = todaySchedule.ishaMillis
            val nextTime = tomorrowSchedule.fajrMillis
            val span = (nextTime - prevTime).coerceAtLeast(1L)
            val elapsed = (nowMillis - prevTime).coerceIn(0L, span)
            NextPrayerInfo(
                nextPrayer = PrayerName.FAJR,
                nextPrayerMillis = nextTime,
                previousPrayer = PrayerName.ISHA,
                previousPrayerMillis = prevTime,
                remainingMillis = (nextTime - nowMillis).coerceAtLeast(0L),
                progress = elapsed.toFloat() / span.toFloat()
            )
        }
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2.0 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun sunPosition(jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sinDeg(g) + 0.020 * sinDeg(2.0 * g))
        val e = 23.439 - 0.00000036 * d

        val ra = atan2Deg(cosDeg(e) * sinDeg(l), cosDeg(l)) / 15.0
        val eqTime = q / 15.0 - fixHour(ra)
        val decl = asinDeg(sinDeg(e) * sinDeg(l))
        return decl to eqTime
    }

    private fun computeMidDay(jd: Double): Double {
        val (_, eqTime) = sunPosition(jd)
        return fixHour(12.0 - eqTime)
    }

    private fun computeSunAngleTime(
        jd: Double,
        altitudeDeg: Double,
        latitude: Double,
        ccw: Boolean
    ): Double {
        val (decl, _) = sunPosition(jd)
        val noon = computeMidDay(jd)
        val cosVal = (sinDeg(altitudeDeg) - sinDeg(decl) * sinDeg(latitude)) /
            (cosDeg(decl) * cosDeg(latitude))
        if (cosVal < -1.0 || cosVal > 1.0) return Double.NaN
        val t = acosDeg(cosVal) / 15.0
        return noon + if (ccw) -t else t
    }

    private fun computeAsrTime(jd: Double, shadowFactor: Double, latitude: Double): Double {
        val (decl, _) = sunPosition(jd)
        val angle = -arccotDeg(shadowFactor + tanDeg(abs(latitude - decl)))
        return computeSunAngleTime(jd, -angle, latitude, ccw = false)
    }

    private fun hoursToMillis(midnightMillis: Long, hours: Double): Long {
        val minutesRounded = kotlin.math.round(hours * 60.0).toLong()
        return midnightMillis + minutesRounded * 60_000L
    }

    private fun normalizeHours(h: Double): Double {
        var x = h % 24.0
        if (x < 0) x += 24.0
        return x
    }

    private fun fixAngle(a: Double): Double {
        var r = a - 360.0 * floor(a / 360.0)
        if (r < 0) r += 360.0
        return r
    }

    private fun fixHour(a: Double): Double {
        var r = a - 24.0 * floor(a / 24.0)
        if (r < 0) r += 24.0
        return r
    }

    private fun sinDeg(d: Double) = sin(d * PI / 180.0)
    private fun cosDeg(d: Double) = cos(d * PI / 180.0)
    private fun tanDeg(d: Double) = tan(d * PI / 180.0)
    private fun asinDeg(x: Double) = asin(x) * 180.0 / PI
    private fun acosDeg(x: Double) = acos(x) * 180.0 / PI
    private fun atan2Deg(y: Double, x: Double) = atan2(y, x) * 180.0 / PI
    private fun arccotDeg(x: Double) = atan(1.0 / x) * 180.0 / PI
}
