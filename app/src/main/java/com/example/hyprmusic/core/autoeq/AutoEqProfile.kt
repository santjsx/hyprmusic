package com.example.hyprmusic.core.autoeq

import androidx.compose.runtime.Immutable

/**
 * High-Precision Headphone Equalization Profile from Jaakko Pasanen's AutoEq project.
 * Compensates for physical acoustic bottlenecks to achieve the Harman Target curve.
 *
 * @param id Unique identifier (e.g., "boat_rockerz_413", "apple_airpods_pro_2")
 * @param brand Headphone manufacturer brand (e.g., "boAt", "Apple", "Sony")
 * @param model Exact headphone model (e.g., "Rockerz 413", "AirPods Pro 2")
 * @param measurementSource Rig measurement authority (e.g., "Crinacle", "oratory1990", "Rtings")
 * @param formFactor Physical driver type ("In-Ear", "Over-Ear", "Earbuds")
 * @param preampDb Attenuation in dB applied before filter stages to guarantee 100% distortion-free headroom
 * @param bandGainsDb 10-band gain offsets in dB for [31Hz, 62Hz, 125Hz, 250Hz, 500Hz, 1kHz, 2kHz, 4kHz, 8kHz, 16kHz]
 * @param description Audiophile listening notes explaining acoustic changes
 * @param rawUrl Optional GitHub source URL for cloud sync
 */
@Immutable
data class AutoEqProfile(
    val id: String,
    val brand: String,
    val model: String,
    val measurementSource: String = "Harman Target",
    val formFactor: String = "Over-Ear",
    val preampDb: Float = -5.0f,
    val bandGainsDb: FloatArray = FloatArray(10) { 0f },
    val description: String = "AutoEq acoustic compensation profile calibrated to the Harman listener preference target.",
    val rawUrl: String? = null
) {
    val displayName: String
        get() = "$brand $model"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AutoEqProfile) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

/**
 * ISO Standard 10-Band Graphic Equalizer Center Frequencies.
 */
object AutoEqFrequencies {
    val BANDS_HZ = intArrayOf(31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000)
    val LABELS = listOf("31", "62", "125", "250", "500", "1k", "2k", "4k", "8k", "16k")
}
