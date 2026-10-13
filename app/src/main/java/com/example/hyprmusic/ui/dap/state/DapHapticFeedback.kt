package com.example.hyprmusic.ui.dap.state

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View

/**
 * 4-Tier Hierarchical Haptic Engine.
 * Matches physical tactile resistance to the user's input type:
 * - Tier 1: Micro-tick for wheel rotation (subtle, non-fatiguing)
 * - Tier 2: Light tap for timeline scrubbing and list item focus
 * - Tier 3: Medium confirmation for quadrant and center button clicks
 * - Tier 4: Heavy pulse for screen drill-down or long-press contextual menus
 */
class DapHapticFeedback(private val view: View) {

    // Tier 1: Micro-tick every 18 degrees of wheel rotation
    fun tick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_TICK)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }
    }

    // Tier 2: Light tap during timeline drag or focused list item movement
    fun lightTap() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.GESTURE_THRESHOLD_ACTIVATE)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }
    }

    // Tier 3: Medium physical press confirmation for click-wheel quadrant/center buttons
    fun buttonClick() {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    // Tier 4: Heavy feedback for navigation transitions, long-press, or mode switches
    fun heavyPulse() {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
}
