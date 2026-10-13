package com.example.hyprmusic.ui.dap.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapTypography

/**
 * Custom illustrated mascot state for empty libraries, empty queues, and offline tracks.
 * Replaces generic empty text screens with visual character and friendly tone.
 */
@Composable
fun DapEmptyState(
    title: String,
    message: String,
    icon: ImageVector = Icons.Default.GraphicEq,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Retro Mascot Badge Container
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(DapColors.ScreenCard)
                .border(1.5.dp, DapColors.ScreenCardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DapColors.AccentIceBlue,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = title,
            style = DapTypography.MenuHeader,
            color = DapColors.TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = message,
            style = DapTypography.MenuSecondary,
            color = DapColors.TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}
