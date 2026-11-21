package com.example.domain.prayer

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Exact spherical trigonometry Qibla direction & Great-Circle distance to the Holy Kaaba in Makkah.
 */
object QiblaCalculator {
    const val KAABA_LAT = 21.422487
    const val KAABA_LON = 39.826206
    private const val EARTH_RADIUS_KM = 6371.0088

    /**
     * Returns bearing in degrees clockwise from True North [0.0, 360.0).
     */
    fun calculateQiblaBearing(latitude: Double, longitude: Double): Double {
        val phi1 = Math.toRadians(latitude)
        val phi2 = Math.toRadians(KAABA_LAT)
        val deltaLambda = Math.toRadians(KAABA_LON - longitude)

        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        val theta = atan2(y, x) * 180.0 / PI
        return (theta + 360.0) % 360.0
    }

    /**
     * Returns great-circle Haversine distance to Makkah in kilometers.
     */
    fun calculateDistanceToMakkahKm(latitude: Double, longitude: Double): Double {
        val phi1 = Math.toRadians(latitude)
        val phi2 = Math.toRadians(KAABA_LAT)
        val dPhi = Math.toRadians(KAABA_LAT - latitude)
        val dLambda = Math.toRadians(KAABA_LON - longitude)

        val a = sin(dPhi / 2.0) * sin(dPhi / 2.0) +
            cos(phi1) * cos(phi2) * sin(dLambda / 2.0) * sin(dLambda / 2.0)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return EARTH_RADIUS_KM * c
    }

    /**
     * Shortest signed angular delta in [-180, 180] between current heading and Qibla bearing.
     */
    fun shortestAngleDelta(currentHeadingDeg: Double, targetBearingDeg: Double): Double {
        var delta = (targetBearingDeg - currentHeadingDeg + 540.0) % 360.0 - 180.0
        if (delta < -180.0) delta += 360.0
        return delta
    }
}
