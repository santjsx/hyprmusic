package com.example.hyprmusic.ui.elovaire.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hyprmusic.ui.components.HyprWorkspace
import com.example.hyprmusic.ui.components.MiniEqualizerBars
import com.example.hyprmusic.ui.elovaire.motion.ElovaireMotionSpecs
import com.example.hyprmusic.ui.elovaire.motion.elovairePressScale
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import com.example.hyprmusic.ui.elovaire.theme.ElovaireRadii
import com.example.hyprmusic.ui.elovaire.theme.ElovaireSpacing

data class ElovaireTabItem(
    val workspace: HyprWorkspace,
    val label: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
)

/**
 * Ergonomic Floating Elovaire Bottom Navigation Dock.
 * Elevated 68dp capsule with translucent gradient backdrop, subtle 1px border,
 * tactile spring bouncing, and animated active indicator dot.
 */
@Composable
fun ElovaireBottomDock(
    currentWorkspace: HyprWorkspace,
    onWorkspaceSelected: (HyprWorkspace) -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    val tabs = remember {
        listOf(
            ElovaireTabItem(HyprWorkspace.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
            ElovaireTabItem(HyprWorkspace.LIBRARY, "Library", Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic),
            ElovaireTabItem(HyprWorkspace.PLAYING, "Player", Icons.Filled.Equalizer, Icons.Filled.Equalizer),
            ElovaireTabItem(HyprWorkspace.SETTINGS, "Settings", Icons.Filled.Tune, Icons.Outlined.Tune)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ElovaireSpacing.bottomDockOuterPad, vertical = 6.dp)
            .clip(RoundedCornerShape(ElovaireRadii.dock))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ElovaireColors.Carbon.copy(alpha = 0.94f),
                        ElovaireColors.Carbon.copy(alpha = 0.98f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = ElovaireColors.BorderSubtle,
                shape = RoundedCornerShape(ElovaireRadii.dock)
            )
            .padding(horizontal = 8.dp, vertical = ElovaireSpacing.bottomDockInnerPad)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = tab.workspace == currentWorkspace

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) ElovaireColors.AzureAccent else ElovaireColors.TextMuted,
                    animationSpec = ElovaireMotionSpecs.overlayEnter(),
                    label = "dock_icon_color"
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) ElovaireColors.TextPrimary else ElovaireColors.TextMuted,
                    animationSpec = ElovaireMotionSpecs.overlayEnter(),
                    label = "dock_text_color"
                )

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1.0f,
                    animationSpec = ElovaireMotionSpecs.pressSpring(),
                    label = "dock_icon_scale"
                )

                val dotAlpha by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0f,
                    animationSpec = ElovaireMotionSpecs.overlayEnter(),
                    label = "dock_dot_alpha"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(ElovaireRadii.tile))
                        .elovairePressScale(pressedScale = 0.90f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                onWorkspaceSelected(tab.workspace)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                        ) {
                            if (tab.workspace == HyprWorkspace.PLAYING && isPlaying) {
                                MiniEqualizerBars(
                                    theme = com.example.hyprmusic.core.theming.HyprThemeConfig(),
                                    isPlaying = true,
                                    modifier = Modifier.size(22.dp).align(Alignment.Center)
                                )
                            } else {
                                Icon(
                                    imageVector = if (isSelected) tab.iconSelected else tab.iconUnselected,
                                    contentDescription = tab.label,
                                    tint = iconColor,
                                    modifier = Modifier.size(21.dp).align(Alignment.Center)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = tab.label,
                            color = textColor,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Active Indicator Dot
                        Box(
                            modifier = Modifier
                                .size(3.5.dp)
                                .graphicsLayer { alpha = dotAlpha }
                                .clip(CircleShape)
                                .background(ElovaireColors.AzureAccent)
                        )
                    }
                }
            }
        }
    }
}
