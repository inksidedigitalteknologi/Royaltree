package com.inkside.digital.sensor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * LocationHelper — deteksi wilayah user.
 *
 * Butuh izin:
 * - ACCESS_FINE_LOCATION (presisi)
 * - ACCESS_COARSE_LOCATION (kasar)
 */
object LocationHelper {

    private const val TAG = "LocationHelper"

    /**
     * Cek apakah izin lokasi sudah diberikan.
     */
    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    /**
     * Cek apakah GPS aktif.
     */
    fun isGpsEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        return try {
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get last known location (tanpa request update).
     */
    suspend fun getLastLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) {
            Log.w(TAG, "No location permission")
            return null
        }

        return suspendCancellableCoroutine { cont ->
            try {
                val client: FusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(context)

                client.lastLocation
                    .addOnSuccessListener { location ->
                        cont.resume(location)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to get location: ${e.message}")
                        cont.resume(null)
                    }
            } catch (e: SecurityException) {
                Log.e(TAG, "Security exception: ${e.message}")
                cont.resume(null)
            }
        }
    }

    /**
     * Get current location (request fresh).
     */
    suspend fun getCurrentLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) return null

        return suspendCancellableCoroutine { cont ->
            try {
                val client: FusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(context)

                client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        cont.resume(location)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to get current location: ${e.message}")
                        cont.resume(null)
                    }
            } catch (e: SecurityException) {
                Log.e(TAG, "Security exception: ${e.message}")
                cont.resume(null)
            }
        }
    }

    /**
     * Get wilayah berdasarkan latitude & longitude.
     * Reverse geocoding sederhana berdasarkan koordinat.
     */
    fun getRegionFromLocation(location: Location?): String {
        if (location == null) return "Unknown"

        val lat = location.latitude
        val lon = location.longitude

        return when {
            // Indonesia
            lat in -11.0..6.0 && lon in 95.0..141.0 -> "Indonesia"
            // Malaysia
            lat in 0.85..7.5 && lon in 99.6..119.3 -> "Malaysia"
            // Singapore
            lat in 1.1..1.5 && lon in 103.6..104.1 -> "Singapore"
            // Thailand
            lat in 5.6..20.5 && lon in 97.3..105.6 -> "Thailand"
            // Philippines
            lat in 4.5..21.0 && lon in 116.0..127.0 -> "Philippines"
            // Vietnam
            lat in 8.0..23.5 && lon in 102.0..110.0 -> "Vietnam"
            else -> "Other"
        }
    }

    /**
     * Get location summary.
     */
    fun getLocationSummary(location: Location?): LocationSummary {
        if (location == null) {
            return LocationSummary(
                latitude = 0.0,
                longitude = 0.0,
                region = "Unknown",
                hasLocation = false
            )
        }

        return LocationSummary(
            latitude = location.latitude,
            longitude = location.longitude,
            region = getRegionFromLocation(location),
            hasLocation = true
        )
    }

    data class LocationSummary(
        val latitude: Double,
        val longitude: Double,
        val region: String,
        val hasLocation: Boolean
    )
}
