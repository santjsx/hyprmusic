package com.example.hyprmusic.ui.elovaire.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.hyprmusic.ui.elovaire.theme.ElovaireColors
import com.example.hyprmusic.ui.elovaire.theme.ElovaireRadii

/**
 * Standard Elovaire Card Container.
 * Clean, dark/light surface with subtle 1px border.
 */
@Composable
fun ElovaireCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = ElovaireRadii.card,
    backgroundColor: Color = ElovaireColors.Carbon,
    borderColor: Color = ElovaireColors.BorderSubtle,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor, shape)
            .border(1.dp, borderColor, shape)
    ) {
        content()
    }
}

/**
 * Inset Grouped List Row Container (iOS / Elovaire signature style).
 * Automatically rounds top corners for the first item, bottom corners for the last item,
 * and leaves middle items square, with an inset divider line.
 */
@Composable
fun GroupedListRowContainer(
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ElovaireColors.Carbon,
    borderColor: Color = ElovaireColors.BorderSubtle,
    content: @Composable () -> Unit
) {
    val shape = when {
        count <= 1 -> RoundedCornerShape(ElovaireRadii.card)
        index == 0 -> RoundedCornerShape(
            topStart = ElovaireRadii.card,
            topEnd = ElovaireRadii.card,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
        index == count - 1 -> RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = ElovaireRadii.card,
            bottomEnd = ElovaireRadii.card
        )
        else -> RoundedCornerShape(0.dp)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor, shape)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
    ) {
        content()
    }
}

/**
 * Inset Grouped Row Divider Line.
 */
@Composable
fun GroupedRowDivider(
    modifier: Modifier = Modifier,
    insetStart: Dp = 68.dp,
    color: Color = ElovaireColors.Divider
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = insetStart, end = 12.dp)
            .height(0.6.dp)
            .background(color)
    )
}
