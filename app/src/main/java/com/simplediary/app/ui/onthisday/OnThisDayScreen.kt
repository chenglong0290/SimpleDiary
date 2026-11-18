package com.simplediary.app.ui.onthisday

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simplediary.app.data.OnThisDayMode
import com.simplediary.app.ui.components.DiaryListItem

@Composable
fun OnThisDayScreen(
    viewModel: OnThisDayViewModel,
    onOpenEditor: (Long) -> Unit
) {
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = modeTitle(mode),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emptyHint(mode),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(entries, key = { it.id }) { entry ->
                        DiaryListItem(
                            entry = entry,
                            onClick = { onOpenEditor(entry.id) },
                            onMoodChange = { viewModel.updateMood(entry.id, it) }
                        )
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { viewModel.cycleMode() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            icon = {
                Icon(Icons.Outlined.SwapHoriz, contentDescription = null)
            },
            text = {
                Text(modeShortLabel(mode))
            }
        )
    }
}

private fun modeTitle(mode: OnThisDayMode): String = when (mode) {
    OnThisDayMode.SAME_DAY_EACH_YEAR -> "那年今日"
    OnThisDayMode.SAME_DAY_EACH_MONTH -> "那月今日"
    OnThisDayMode.SAME_WEEKDAY_EACH_WEEK -> "那周今日"
}

private fun modeShortLabel(mode: OnThisDayMode): String = when (mode) {
    OnThisDayMode.SAME_DAY_EACH_YEAR -> "切到那月"
    OnThisDayMode.SAME_DAY_EACH_MONTH -> "切到那周"
    OnThisDayMode.SAME_WEEKDAY_EACH_WEEK -> "切到那年"
}

private fun emptyHint(mode: OnThisDayMode): String = when (mode) {
    OnThisDayMode.SAME_DAY_EACH_YEAR -> "还没有往年今天的日记"
    OnThisDayMode.SAME_DAY_EACH_MONTH -> "还没有其他月份同一天的日记"
    OnThisDayMode.SAME_WEEKDAY_EACH_WEEK -> "还没有其他周同一星期几的日记"
}
