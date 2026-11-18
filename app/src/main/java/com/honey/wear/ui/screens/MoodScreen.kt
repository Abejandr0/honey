package com.honey.wear.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.honey.wear.R
import com.honey.wear.data.MoodType
import com.honey.wear.ui.components.HoneyBackChip
import com.honey.wear.ui.components.HoneyIconTile
import com.honey.wear.ui.components.moodIcon

@Composable
fun MoodScreen(
    currentMood: MoodType?,
    onSelectMood: (MoodType) -> Unit,
    onBack: () -> Unit
) {
    val moods = MoodType.entries.toList()

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.mood_title),
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }
        item {
            Text(
                text = stringResource(R.string.mood_subtitle),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                fontSize = 9.sp,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }

        moods.chunked(2).forEach { pair ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    pair.forEach { mood ->
                        HoneyIconTile(
                            label = stringResource(mood.labelRes),
                            icon = moodIcon(mood),
                            selected = mood == currentMood,
                            modifier = Modifier.weight(1f),
                            onClick = { onSelectMood(mood) }
                        )
                    }
                }
            }
        }

        item { HoneyBackChip(stringResource(R.string.back), onBack) }
    }
}
