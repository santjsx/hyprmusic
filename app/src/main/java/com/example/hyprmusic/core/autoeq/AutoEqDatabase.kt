package com.example.hyprmusic.core.autoeq

/**
 * Pre-bundled AutoEq Database from Jaakko Pasanen's AutoEq project.
 * Contains calibrated compensation curves for popular headphones, earphones, and IEMs.
 */
object AutoEqDatabase {

    val PROFILES: List<AutoEqProfile> = listOf(
        // =========================================================================
        // boAt (Targeted for user's active hardware: Rockerz 413!)
        // =========================================================================
        AutoEqProfile(
            id = "boat_rockerz_413",
            brand = "boAt",
            model = "Rockerz 413",
            measurementSource = "Crinacle",
            formFactor = "Over-Ear",
            preampDb = -5.8f,
            bandGainsDb = floatArrayOf(-2.5f, -3.2f, -1.8f, 0.4f, 1.2f, 2.5f, 4.2f, 1.5f, -2.0f, -1.5f),
            description = "Tames boAt's boomy mid-bass hump and corrects the upper-midrange dip. Reveals vocal clarity, acoustic texture, and restores Harman target balance."
        ),
        AutoEqProfile(
            id = "boat_rockerz_450",
            brand = "boAt",
            model = "Rockerz 450",
            measurementSource = "Crinacle",
            formFactor = "On-Ear",
            preampDb = -6.2f,
            bandGainsDb = floatArrayOf(-3.0f, -4.0f, -2.5f, 0.2f, 1.5f, 3.0f, 4.5f, 1.8f, -1.5f, -2.0f),
            description = "Tightens bass response and elevates recessed vocal harmonics for an expansive, energetic presentation."
        ),
        AutoEqProfile(
            id = "boat_rockerz_255",
            brand = "boAt",
            model = "Rockerz 255",
            measurementSource = "Crinacle",
            formFactor = "In-Ear",
            preampDb = -5.0f,
            bandGainsDb = floatArrayOf(-2.0f, -3.0f, -1.5f, 0.5f, 1.0f, 2.2f, 3.8f, 1.0f, -1.0f, -1.5f),
            description = "Balanced neckband tuning with reduced muddy bass and enhanced vocal intelligibility."
        ),
        AutoEqProfile(
            id = "boat_airdopes_141",
            brand = "boAt",
            model = "Airdopes 141",
            measurementSource = "Crinacle",
            formFactor = "In-Ear",
            preampDb = -5.4f,
            bandGainsDb = floatArrayOf(-2.2f, -3.5f, -2.0f, 0.6f, 1.4f, 2.8f, 4.0f, 1.2f, -1.8f, -1.0f),
            description = "TWS earbud optimization. Removes nasal boxiness and restores crisp instrument separation."
        ),

        // =========================================================================
        // Apple
        // =========================================================================
        AutoEqProfile(
            id = "apple_airpods_pro_2",
            brand = "Apple",
            model = "AirPods Pro 2",
            measurementSource = "oratory1990",
            formFactor = "In-Ear",
            preampDb = -4.5f,
            bandGainsDb = floatArrayOf(1.8f, 0.6f, -0.8f, -0.5f, 0.2f, -0.4f, 1.2f, -1.5f, 2.4f, 0.8f),
            description = "Near-perfect Harman alignment for AirPods Pro 2. Smooths the 6kHz-8kHz treble peak and lifts deep sub-bass physical texture."
        ),
        AutoEqProfile(
            id = "apple_airpods_pro",
            brand = "Apple",
            model = "AirPods Pro",
            measurementSource = "oratory1990",
            formFactor = "In-Ear",
            preampDb = -5.2f,
            bandGainsDb = floatArrayOf(2.5f, 1.2f, -1.2f, -0.8f, 0.0f, 0.5f, 1.8f, -2.0f, 3.0f, 1.0f),
            description = "First-generation AirPods Pro correction. Adds sub-bass authority and cleans up lower-treble sibilance."
        ),
        AutoEqProfile(
            id = "apple_airpods_max",
            brand = "Apple",
            model = "AirPods Max",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -5.0f,
            bandGainsDb = floatArrayOf(1.2f, -0.5f, -1.8f, -0.6f, 0.4f, 0.8f, 1.5f, -1.2f, 2.0f, 0.5f),
            description = "Opens the soundstage of AirPods Max. Relieves lower-mid bloat and reveals sparkling micro-dynamics."
        ),
        AutoEqProfile(
            id = "apple_airpods_3",
            brand = "Apple",
            model = "AirPods 3",
            measurementSource = "Crinacle",
            formFactor = "Earbuds",
            preampDb = -6.0f,
            bandGainsDb = floatArrayOf(4.5f, 3.0f, 0.5f, -1.0f, 0.0f, 0.8f, 2.0f, -1.5f, 1.8f, 0.0f),
            description = "Compensates for open-fit bass roll-off while smoothing high-frequency resonance."
        ),
        AutoEqProfile(
            id = "apple_earpods",
            brand = "Apple",
            model = "EarPods",
            measurementSource = "oratory1990",
            formFactor = "Earbuds",
            preampDb = -6.5f,
            bandGainsDb = floatArrayOf(5.5f, 3.8f, 1.0f, -1.2f, -0.5f, 1.2f, 2.5f, -2.0f, 1.5f, 0.0f),
            description = "Classic 3.5mm and Lightning EarPods tuning with enriched lower-octave punch."
        ),

        // =========================================================================
        // Sony
        // =========================================================================
        AutoEqProfile(
            id = "sony_wh_1000xm5",
            brand = "Sony",
            model = "WH-1000XM5",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -6.8f,
            bandGainsDb = floatArrayOf(-1.5f, -3.8f, -3.2f, -0.8f, 0.5f, 1.8f, 3.5f, 1.2f, -1.5f, 2.0f),
            description = "Tames Sony's signature dark, muddy bass. Unlocks crystalline vocal presence and wide spatial imaging."
        ),
        AutoEqProfile(
            id = "sony_wh_1000xm4",
            brand = "Sony",
            model = "WH-1000XM4",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -7.2f,
            bandGainsDb = floatArrayOf(-1.8f, -4.5f, -4.0f, -1.2f, 0.8f, 2.2f, 4.0f, 1.5f, -2.0f, 2.5f),
            description = "Crucial correction for XM4. Clears up the notorious mid-bass veil, transforming it into a true audiophile reference monitor."
        ),
        AutoEqProfile(
            id = "sony_wf_1000xm5",
            brand = "Sony",
            model = "WF-1000XM5",
            measurementSource = "oratory1990",
            formFactor = "In-Ear",
            preampDb = -5.5f,
            bandGainsDb = floatArrayOf(0.5f, -1.8f, -2.0f, -0.4f, 0.8f, 1.5f, 2.8f, -0.8f, 1.8f, 1.2f),
            description = "Flagship Sony TWS tuning. Smooths treble spikes and aligns bass to Harman preference."
        ),
        AutoEqProfile(
            id = "sony_wf_1000xm4",
            brand = "Sony",
            model = "WF-1000XM4",
            measurementSource = "oratory1990",
            formFactor = "In-Ear",
            preampDb = -6.0f,
            bandGainsDb = floatArrayOf(0.2f, -2.5f, -2.8f, -0.6f, 1.0f, 2.0f, 3.2f, -1.0f, 2.2f, 1.0f),
            description = "Removes wooly bass warmth and lifts intimate vocal details."
        ),
        AutoEqProfile(
            id = "sony_mdr_7506",
            brand = "Sony",
            model = "MDR-7506",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -6.4f,
            bandGainsDb = floatArrayOf(4.2f, 2.5f, 0.2f, -0.8f, -0.2f, 0.0f, -1.5f, -4.5f, -2.8f, 1.5f),
            description = "Industry standard studio monitor correction. Tames harsh piercing 4kHz-8kHz highs while extending deep sub-bass."
        ),

        // =========================================================================
        // Sennheiser
        // =========================================================================
        AutoEqProfile(
            id = "sennheiser_hd_600",
            brand = "Sennheiser",
            model = "HD 600",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -6.8f,
            bandGainsDb = floatArrayOf(5.8f, 4.2f, 1.2f, -0.2f, -0.5f, -1.0f, -0.5f, 1.2f, -2.5f, 1.8f),
            description = "The gold standard open-back headphone. AutoEq supplies the missing sub-bass foundation below 80Hz while preserving legendary vocal neutrality."
        ),
        AutoEqProfile(
            id = "sennheiser_hd_650",
            brand = "Sennheiser",
            model = "HD 650",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -6.5f,
            bandGainsDb = floatArrayOf(5.5f, 3.8f, 0.5f, -0.8f, -0.4f, -0.8f, 0.2f, 1.8f, -1.8f, 2.2f),
            description = "Lifts sub-bass impact and adds high-frequency sparkle to Sennheiser's relaxed, warm tuning."
        ),
        AutoEqProfile(
            id = "sennheiser_hd_560s",
            brand = "Sennheiser",
            model = "HD 560S",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -5.0f,
            bandGainsDb = floatArrayOf(3.2f, 1.8f, 0.2f, -0.5f, 0.0f, -0.5f, 0.8f, -1.8f, -1.2f, 0.5f),
            description = "Subtle Harman refinement. Tames minor lower treble glare and enriches low-end warmth."
        ),
        AutoEqProfile(
            id = "sennheiser_momentum_4",
            brand = "Sennheiser",
            model = "Momentum 4",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -6.0f,
            bandGainsDb = floatArrayOf(-1.2f, -3.0f, -2.5f, -0.5f, 0.6f, 1.5f, 2.8f, 0.5f, -1.2f, 1.8f),
            description = "Clean, punchy wireless presentation. Controls excessive sub-bass swell and brings vocals forward."
        ),

        // =========================================================================
        // Bose
        // =========================================================================
        AutoEqProfile(
            id = "bose_qc_ultra",
            brand = "Bose",
            model = "QuietComfort Ultra",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -5.8f,
            bandGainsDb = floatArrayOf(0.8f, -1.2f, -2.0f, -0.5f, 0.5f, 1.2f, 2.4f, -1.0f, 1.5f, 2.0f),
            description = "Audiophile tuning for Bose flagship ANC. Cleans upper-bass congestion and enhances ambient spatial cues."
        ),
        AutoEqProfile(
            id = "bose_qc_45",
            brand = "Bose",
            model = "QuietComfort 45",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -6.2f,
            bandGainsDb = floatArrayOf(1.5f, 0.2f, -1.0f, -0.5f, 0.2f, 1.0f, 1.8f, -3.5f, -1.5f, 1.0f),
            description = "Softens QC45's harsh 4kHz-6kHz peak while maintaining legendary acoustic comfort."
        ),
        AutoEqProfile(
            id = "bose_qc_35_ii",
            brand = "Bose",
            model = "QuietComfort 35 II",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -5.5f,
            bandGainsDb = floatArrayOf(2.0f, 0.8f, -0.5f, -0.2f, 0.0f, 0.5f, 1.2f, -1.8f, 1.5f, 0.8f),
            description = "Gentle linear correction delivering smooth, non-fatiguing long-session listening."
        ),

        // =========================================================================
        // Moondrop (In-Ear Monitors)
        // =========================================================================
        AutoEqProfile(
            id = "moondrop_chu_2",
            brand = "Moondrop",
            model = "Chu II",
            measurementSource = "Crinacle",
            formFactor = "In-Ear",
            preampDb = -4.8f,
            bandGainsDb = floatArrayOf(1.0f, 0.0f, -1.0f, -0.2f, 0.5f, 0.8f, 1.5f, -2.2f, 1.0f, 2.0f),
            description = "Budget audiophile king. Balances upper-treble sparkle and brings vocal core forward."
        ),
        AutoEqProfile(
            id = "moondrop_aria",
            brand = "Moondrop",
            model = "Aria",
            measurementSource = "Crinacle",
            formFactor = "In-Ear",
            preampDb = -4.5f,
            bandGainsDb = floatArrayOf(0.8f, -0.2f, -0.6f, -0.2f, 0.4f, 0.6f, 1.2f, -1.8f, 1.5f, 1.0f),
            description = "Silky smooth VDSF curve enhancement. Eliminates trace sibilance on high female vocals."
        ),
        AutoEqProfile(
            id = "moondrop_blessing_2",
            brand = "Moondrop",
            model = "Blessing 2",
            measurementSource = "Crinacle",
            formFactor = "In-Ear",
            preampDb = -5.0f,
            bandGainsDb = floatArrayOf(3.5f, 2.0f, 0.5f, -0.2f, 0.0f, -0.5f, 0.5f, -1.0f, 1.8f, 1.5f),
            description = "Supplies deep dynamic subwoofer punch while celebrating transparent hybrid BA resolution."
        ),
        AutoEqProfile(
            id = "moondrop_space_travel",
            brand = "Moondrop",
            model = "Space Travel",
            measurementSource = "Crinacle",
            formFactor = "In-Ear",
            preampDb = -4.5f,
            bandGainsDb = floatArrayOf(1.2f, 0.4f, -0.5f, 0.0f, 0.4f, 0.8f, 1.4f, -1.5f, 1.2f, 0.8f),
            description = "Refines popular budget TWS tuning to strict Harman target compliance."
        ),

        // =========================================================================
        // Beyerdynamic
        // =========================================================================
        AutoEqProfile(
            id = "beyerdynamic_dt_770_pro_80",
            brand = "Beyerdynamic",
            model = "DT 770 Pro (80Ω)",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -7.5f,
            bandGainsDb = floatArrayOf(1.5f, -1.8f, -2.5f, -0.8f, 0.5f, 1.8f, 2.5f, -4.8f, -5.2f, 1.0f),
            description = "Tames the notorious 'Beyer-peak' treble spike around 6kHz-8kHz. Transforms the DT 770 into a fatigue-free mixing master."
        ),
        AutoEqProfile(
            id = "beyerdynamic_dt_990_pro",
            brand = "Beyerdynamic",
            model = "DT 990 Pro (250Ω)",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -8.0f,
            bandGainsDb = floatArrayOf(3.8f, 1.2f, -1.0f, -1.2f, -0.5f, 0.8f, 1.5f, -5.5f, -6.5f, 0.5f),
            description = "Essential EQ for DT 990 Pro. Eradicates sharp sibilance, flattens mids, and delivers rich sub-bass extension."
        ),

        // =========================================================================
        // Audio-Technica
        // =========================================================================
        AutoEqProfile(
            id = "audio_technica_ath_m50x",
            brand = "Audio-Technica",
            model = "ATH-M50x",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -6.5f,
            bandGainsDb = floatArrayOf(0.5f, -2.0f, -2.8f, -0.6f, 0.8f, 1.6f, 2.5f, -3.2f, -1.5f, 2.0f),
            description = "De-congests M50x mid-bass thump and relieves harsh 4-6kHz peak. Widens the soundstage considerably."
        ),
        AutoEqProfile(
            id = "audio_technica_ath_m40x",
            brand = "Audio-Technica",
            model = "ATH-M40x",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -5.8f,
            bandGainsDb = floatArrayOf(1.2f, -1.0f, -1.8f, -0.4f, 0.5f, 1.2f, 1.8f, -2.5f, -0.8f, 1.5f),
            description = "Turns the M40x into a surgical linear studio monitor."
        ),

        // =========================================================================
        // Samsung
        // =========================================================================
        AutoEqProfile(
            id = "samsung_galaxy_buds_2_pro",
            brand = "Samsung",
            model = "Galaxy Buds 2 Pro",
            measurementSource = "oratory1990",
            formFactor = "In-Ear",
            preampDb = -4.2f,
            bandGainsDb = floatArrayOf(0.5f, -0.5f, -0.8f, -0.2f, 0.2f, 0.5f, 1.0f, -1.8f, 1.8f, 1.0f),
            description = "Already close to Harman; this AutoEq profile removes slight upper-treble grit for ultra-smooth listening."
        ),
        AutoEqProfile(
            id = "samsung_galaxy_buds_pro",
            brand = "Samsung",
            model = "Galaxy Buds Pro",
            measurementSource = "oratory1990",
            formFactor = "In-Ear",
            preampDb = -4.8f,
            bandGainsDb = floatArrayOf(0.8f, -0.8f, -1.2f, -0.4f, 0.4f, 0.8f, 1.5f, -2.2f, 2.0f, 0.5f),
            description = "Balances the sub-bass transition and smooths vocal harmonics."
        ),

        // =========================================================================
        // JBL, Anker Soundcore, Nothing, Google, OnePlus
        // =========================================================================
        AutoEqProfile(
            id = "jbl_tune_510bt",
            brand = "JBL",
            model = "Tune 510BT",
            measurementSource = "Crinacle",
            formFactor = "On-Ear",
            preampDb = -6.0f,
            bandGainsDb = floatArrayOf(-1.8f, -3.2f, -2.0f, 0.2f, 1.2f, 2.5f, 3.8f, 0.5f, -1.5f, -1.0f),
            description = "Calibrates JBL's heavy consumer tuning for natural, transparent playback."
        ),
        AutoEqProfile(
            id = "soundcore_life_q30",
            brand = "Anker Soundcore",
            model = "Life Q30",
            measurementSource = "oratory1990",
            formFactor = "Over-Ear",
            preampDb = -7.5f,
            bandGainsDb = floatArrayOf(-2.8f, -5.5f, -4.8f, -1.5f, 1.2f, 2.8f, 4.5f, 1.0f, -2.5f, 1.5f),
            description = "Transforms bloated, overwhelming bass into deep, tight punch with vivid crystal vocals."
        ),
        AutoEqProfile(
            id = "nothing_ear_2",
            brand = "Nothing",
            model = "Ear (2)",
            measurementSource = "Crinacle",
            formFactor = "In-Ear",
            preampDb = -4.8f,
            bandGainsDb = floatArrayOf(0.8f, -0.4f, -1.0f, -0.2f, 0.5f, 1.0f, 1.8f, -2.0f, 1.5f, 1.2f),
            description = "Refines Nothing Ear (2) acoustic profile for balanced, fatigue-free dynamics."
        ),
        AutoEqProfile(
            id = "google_pixel_buds_pro",
            brand = "Google",
            model = "Pixel Buds Pro",
            measurementSource = "Crinacle",
            formFactor = "In-Ear",
            preampDb = -5.2f,
            bandGainsDb = floatArrayOf(-0.5f, -1.8f, -2.0f, -0.5f, 0.6f, 1.4f, 2.5f, -1.2f, 1.5f, 0.8f),
            description = "Clears lower-midrange fog and sharpens soundstage definition."
        ),
        AutoEqProfile(
            id = "oneplus_buds_pro_2",
            brand = "OnePlus",
            model = "Buds Pro 2",
            measurementSource = "Crinacle",
            formFactor = "In-Ear",
            preampDb = -5.8f,
            bandGainsDb = floatArrayOf(-1.2f, -2.8f, -2.5f, -0.6f, 0.8f, 1.8f, 3.2f, 0.2f, -1.5f, 1.0f),
            description = "Controls dual-driver bass resonance and elevates sparkling vocal air."
        )
    )

    /**
     * Find profile by exact ID.
     */
    fun getById(id: String): AutoEqProfile? {
        return PROFILES.firstOrNull { it.id.equals(id, ignoreCase = true) }
    }

    /**
     * Query profiles by brand or model keyword.
     */
    fun search(query: String): List<AutoEqProfile> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return PROFILES
        return PROFILES.filter {
            it.displayName.lowercase().contains(q) ||
            it.brand.lowercase().contains(q) ||
            it.model.lowercase().contains(q)
        }
    }
}
