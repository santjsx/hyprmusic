package com.example.hyprmusic.core.autoeq

/**
 * Intelligent Bluetooth Device & Headset Auto-Matcher for AutoEq.
 * Inspects connected audio device strings (e.g. "Rockerz 413", "kilobyte's AirPods", "WH-1000XM4")
 * and extracts brand/model tokens to automatically suggest the calibrated AutoEq profile.
 */
object AutoEqMatcher {

    fun findBestMatch(deviceName: String): AutoEqProfile? {
        val raw = deviceName.trim().lowercase()
        if (raw.isBlank() || raw == "phone speaker" || raw == "wired headphones" || raw == "built-in speaker") {
            return null
        }

        // Clean common noise prefixes and suffixes
        val cleaned = raw
            .replace("’s", "")
            .replace("'s", "")
            .replace("headphones", "")
            .replace("earphones", "")
            .replace("wireless", "")
            .replace("hands-free", "")
            .replace("stereo", "")
            .replace("bluetooth", "")
            .trim()

        // 1. Direct model keyword matching against database
        for (profile in AutoEqDatabase.PROFILES) {
            val modelLower = profile.model.lowercase()
            // e.g. "rockerz 413" or "413" in "rockerz 413"
            if (cleaned.contains(modelLower) || modelLower.contains(cleaned)) {
                return profile
            }
        }

        // 2. Specific Brand & Model heuristics
        if (cleaned.contains("rockerz") || cleaned.contains("boat")) {
            if (cleaned.contains("413")) return AutoEqDatabase.getById("boat_rockerz_413")
            if (cleaned.contains("450")) return AutoEqDatabase.getById("boat_rockerz_450")
            if (cleaned.contains("255")) return AutoEqDatabase.getById("boat_rockerz_255")
            if (cleaned.contains("141") || cleaned.contains("airdopes")) return AutoEqDatabase.getById("boat_airdopes_141")
            return AutoEqDatabase.getById("boat_rockerz_413") // Default boAt Rockerz match
        }

        if (cleaned.contains("airpod") || cleaned.contains("airpods")) {
            if (cleaned.contains("max")) return AutoEqDatabase.getById("apple_airpods_max")
            if (cleaned.contains("pro 2") || cleaned.contains("pro (2")) return AutoEqDatabase.getById("apple_airpods_pro_2")
            if (cleaned.contains("pro")) return AutoEqDatabase.getById("apple_airpods_pro")
            if (cleaned.contains("3")) return AutoEqDatabase.getById("apple_airpods_3")
            return AutoEqDatabase.getById("apple_airpods_pro_2")
        }

        if (cleaned.contains("1000xm5") || cleaned.contains("xm5")) {
            return if (cleaned.contains("wf")) AutoEqDatabase.getById("sony_wf_1000xm5") else AutoEqDatabase.getById("sony_wh_1000xm5")
        }
        if (cleaned.contains("1000xm4") || cleaned.contains("xm4")) {
            return if (cleaned.contains("wf")) AutoEqDatabase.getById("sony_wf_1000xm4") else AutoEqDatabase.getById("sony_wh_1000xm4")
        }
        if (cleaned.contains("7506")) return AutoEqDatabase.getById("sony_mdr_7506")

        if (cleaned.contains("hd 600") || cleaned.contains("hd600")) return AutoEqDatabase.getById("sennheiser_hd_600")
        if (cleaned.contains("hd 650") || cleaned.contains("hd650")) return AutoEqDatabase.getById("sennheiser_hd_650")
        if (cleaned.contains("hd 560") || cleaned.contains("hd560s")) return AutoEqDatabase.getById("sennheiser_hd_560s")
        if (cleaned.contains("momentum 4")) return AutoEqDatabase.getById("sennheiser_momentum_4")

        if (cleaned.contains("qc ultra") || cleaned.contains("quietcomfort ultra")) return AutoEqDatabase.getById("bose_qc_ultra")
        if (cleaned.contains("qc 45") || cleaned.contains("qc45")) return AutoEqDatabase.getById("bose_qc_45")
        if (cleaned.contains("qc 35") || cleaned.contains("qc35")) return AutoEqDatabase.getById("bose_qc_35_ii")

        if (cleaned.contains("dt 770") || cleaned.contains("dt770")) return AutoEqDatabase.getById("beyerdynamic_dt_770_pro_80")
        if (cleaned.contains("dt 990") || cleaned.contains("dt990")) return AutoEqDatabase.getById("beyerdynamic_dt_990_pro")

        if (cleaned.contains("m50x") || cleaned.contains("ath-m50x")) return AutoEqDatabase.getById("audio_technica_ath_m50x")
        if (cleaned.contains("m40x") || cleaned.contains("ath-m40x")) return AutoEqDatabase.getById("audio_technica_ath_m40x")

        if (cleaned.contains("chu 2") || cleaned.contains("chu ii")) return AutoEqDatabase.getById("moondrop_chu_2")
        if (cleaned.contains("aria")) return AutoEqDatabase.getById("moondrop_aria")
        if (cleaned.contains("blessing")) return AutoEqDatabase.getById("moondrop_blessing_2")

        if (cleaned.contains("galaxy buds 2 pro") || cleaned.contains("buds2 pro")) return AutoEqDatabase.getById("samsung_galaxy_buds_2_pro")
        if (cleaned.contains("galaxy buds") || cleaned.contains("buds pro")) return AutoEqDatabase.getById("samsung_galaxy_buds_pro")

        return null
    }
}
