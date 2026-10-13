package com.example.hyprmusic.ui.dap.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hyprmusic.ui.dap.state.DapScreen
import com.example.hyprmusic.ui.dap.theme.DapColors
import com.example.hyprmusic.ui.dap.theme.DapTypography

data class DapMenuItem(
    val title: String,
    val subtitle: String? = null,
    val screen: DapScreen
)

/**
 * Classic hierarchical LCD menu display.
 * Designed for rotary wheel scrolling and center-button selection.
 */
@Composable
fun DapMenuDisplay(
    selectedIndex: Int,
    listState: LazyListState,
    onItemClick: (DapScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        DapMenuItem("Now Playing", "Active Track", DapScreen.NOW_PLAYING),
        DapMenuItem("Tracks", "All Audio Files", DapScreen.TRACKS_LIST),
        DapMenuItem("Albums", "By Collection", DapScreen.ALBUMS_LIST),
        DapMenuItem("Artists", "Discographies", DapScreen.ARTISTS_LIST),
        DapMenuItem("Playlists", "Custom Mixes", DapScreen.PLAYLISTS_LIST),
        DapMenuItem("Queue", "Up Next", DapScreen.QUEUE),
        DapMenuItem("Lyrics", "Synchronized LRC", DapScreen.LYRICS),
        DapMenuItem("Equalizer", "10-Band DSP", DapScreen.EQUALIZER),
        DapMenuItem("Settings", "Preferences & Scan", DapScreen.SETTINGS)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // LCD Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "HyprDAP",
                style = DapTypography.MenuHeader,
                color = DapColors.TextPrimary
            )
            Text(
                text = "${selectedIndex + 1}/${menuItems.size}",
                style = DapTypography.TimeCounter,
                color = DapColors.TextSecondary
            )
        }

        HorizontalDivider(
            color = DapColors.ScreenCardBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(menuItems) { index, item ->
                val isSelected = index == selectedIndex

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) DapColors.ScreenCard else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable { onItemClick(item.screen) }
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = DapTypography.MenuItem,
                            color = if (isSelected) DapColors.AccentIceBlue else DapColors.TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        if (item.subtitle != null) {
                            Text(
                                text = item.subtitle,
                                style = DapTypography.MenuSecondary,
                                color = DapColors.TextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = if (isSelected) DapColors.AccentIceBlue else DapColors.TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
