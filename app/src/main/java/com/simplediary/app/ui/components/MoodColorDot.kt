package com.simplediary.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simplediary.app.data.MoodColors

@Composable
fun MoodColorPicker(
    selected: Long,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MoodColors.palette.forEach { color ->
            val isSelected = color == selected
            // White (and any very light swatch) needs an outline to stay visible.
            val outline = when {
                isSelected -> MaterialTheme.colorScheme.onSurface
                MoodColors.isWhite(color) -> MaterialTheme.colorScheme.outline
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(color))
                    .border(
                        width = if (isSelected || MoodColors.isWhite(color)) 2.dp else 0.dp,
                        color = outline,
                        shape = CircleShape
                    )
                    .clickable { onSelect(color) }
            )
        }
    }
}
