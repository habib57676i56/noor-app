package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.domain.model.CityLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return coarse || fine
    }

    @SuppressLint("MissingPermission")
    suspend fun detectCurrentCity(context: Context): CityLocation? {
        if (!hasLocationPermission(context)) return null
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        val location: Location? = suspendCancellableCoroutine { cont ->
            val cts = CancellationTokenSource()
            cont.invokeOnCancellation { cts.cancel() }
            fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                .addOnSuccessListener { loc ->
                    if (loc != null) {
                        if (cont.isActive) cont.resume(loc)
                    } else {
                        fusedClient.lastLocation
                            .addOnSuccessListener { last ->
                                if (cont.isActive) cont.resume(last)
                            }
                            .addOnFailureListener {
                                if (cont.isActive) cont.resume(null)
                            }
                    }
                }
                .addOnFailureListener {
                    if (cont.isActive) cont.resume(null)
                }
        }

        if (location == null) return null

        val lat = location.latitude
        val lon = location.longitude
        val tzId = TimeZone.getDefault().id

        val (cityAr, countryAr) = reverseGeocode(context, lat, lon, Locale("ar"))
        val (cityEn, countryEn) = reverseGeocode(context, lat, lon, Locale.ENGLISH)

        return CityLocation(
            id = "gps_${String.format(Locale.US, "%.2f_%.2f", lat, lon)}",
            nameAr = cityAr ?: cityEn ?: "موقعي الحالي",
            nameEn = cityEn ?: cityAr ?: "Current Location",
            countryAr = countryAr ?: countryEn ?: "",
            countryEn = countryEn ?: countryAr ?: "",
            latitude = lat,
            longitude = lon,
            timezoneId = tzId
        )
    }

    @Suppress("DEPRECATION")
    private suspend fun reverseGeocode(
        context: Context,
        lat: Double,
        lon: Double,
        locale: Locale
    ): Pair<String?, String?> = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, locale)
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            val addr = addresses?.firstOrNull()
            val city = addr?.locality ?: addr?.subAdminArea ?: addr?.adminArea
            val country = addr?.countryName
            city to country
        } catch (_: Exception) {
            null to null
        }
    }
}
