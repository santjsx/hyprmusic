package com.example.hyprmusic.core.autoeq

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Cloud client and parser for Jaakko Pasanen's AutoEq GitHub repository (jaakkopasanen/AutoEq).
 * Allows downloading and parsing FixedBandEQ.txt for 4,000+ headphones measured by Oratory1990/Crinacle/Rtings.
 */
class AutoEqCloudRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("autoeq_cache", Context.MODE_PRIVATE)

    /**
     * Parses a standard AutoEq FixedBandEQ.txt content into an AutoEqProfile.
     *
     * Format example:
     * Preamp: -5.8 dB
     * Filter 1: ON PK Fc 31 Hz Gain -2.5 dB Q 1.41
     * ...
     */
    fun parseFixedBandEq(content: String, brand: String, model: String, source: String = "AutoEq Cloud"): AutoEqProfile? {
        try {
            var preamp = -5.0f
            val gains = FloatArray(10) { 0f }
            val lines = content.lines()

            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("Preamp:", ignoreCase = true)) {
                    val parts = trimmed.split(" ")
                    if (parts.size >= 2) {
                        preamp = parts[1].toFloatOrNull() ?: -5.0f
                    }
                } else if (trimmed.startsWith("Filter", ignoreCase = true)) {
                    // Filter X: ON PK Fc <freq> Hz Gain <gain> dB Q <q>
                    val gainIdx = trimmed.indexOf("Gain", ignoreCase = true)
                    val hzIdx = trimmed.indexOf("Hz", ignoreCase = true)
                    val fcIdx = trimmed.indexOf("Fc", ignoreCase = true)

                    if (gainIdx != -1 && fcIdx != -1 && hzIdx != -1) {
                        val freqStr = trimmed.substring(fcIdx + 2, hzIdx).trim()
                        val freq = freqStr.toIntOrNull() ?: 1000

                        val afterGain = trimmed.substring(gainIdx + 4).trim()
                        val gainParts = afterGain.split(" ")
                        val gainVal = gainParts.firstOrNull()?.toFloatOrNull() ?: 0f

                        // Map to nearest 10-band ISO frequency
                        val bandIndex = findNearestBandIndex(freq)
                        if (bandIndex in 0..9) {
                            gains[bandIndex] = gainVal
                        }
                    }
                }
            }

            val id = "cloud_${brand.lowercase().replace(" ", "_")}_${model.lowercase().replace(" ", "_")}"
            return AutoEqProfile(
                id = id,
                brand = brand,
                model = model,
                measurementSource = source,
                formFactor = "Headphones",
                preampDb = preamp,
                bandGainsDb = gains,
                description = "Custom AutoEq profile loaded from Jaakko Pasanen's AutoEq database."
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun findNearestBandIndex(targetHz: Int): Int {
        var closestIdx = 0
        var minDiff = Int.MAX_VALUE
        for (i in AutoEqFrequencies.BANDS_HZ.indices) {
            val diff = Math.abs(AutoEqFrequencies.BANDS_HZ[i] - targetHz)
            if (diff < minDiff) {
                minDiff = diff
                closestIdx = i
            }
        }
        return closestIdx
    }

    /**
     * Fetch raw FixedBandEQ.txt from GitHub or custom URL.
     */
    suspend fun fetchFromUrl(rawUrl: String, brand: String, model: String): AutoEqProfile? = withContext(Dispatchers.IO) {
        try {
            val url = URL(rawUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "HyprMusic-AutoEq/2.5")

            if (connection.responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val content = reader.readText()
                reader.close()
                val profile = parseFixedBandEq(content, brand, model)
                if (profile != null) {
                    saveProfileToCache(profile)
                }
                profile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveProfileToCache(profile: AutoEqProfile) {
        val serializedGains = profile.bandGainsDb.joinToString(",")
        prefs.edit()
            .putString("p_${profile.id}_name", profile.displayName)
            .putFloat("p_${profile.id}_preamp", profile.preampDb)
            .putString("p_${profile.id}_gains", serializedGains)
            .apply()
    }
}
