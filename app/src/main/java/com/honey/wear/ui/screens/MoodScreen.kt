package com.honey.wear.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.honey.wear.R
import com.honey.wear.data.MoodType

@Composable
fun MoodScreen(
    currentMood: MoodType,
    onSelectMood: (MoodType) -> Unit,
    onBack: () -> Unit
) {
    val moods = listOf(
        MoodType.HAPPY,
        MoodType.IN_LOVE,
        MoodType.TIRED,
        MoodType.CUDDLE,
        MoodType.STRESSED,
        MoodType.PLAYFUL
    )

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.mood_title),
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }

        // Two columns of moods
        moods.chunked(2).forEach { pair ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    pair.forEach { m ->
                        val isSelected = m == currentMood
                        Chip(
                            onClick = { onSelectMood(m) },
                            label = { Text(stringResource(m.labelRes), fontSize = 11.sp) },
                            icon = { Text(m.emoji, fontSize = 18.sp) },
                            colors = if (isSelected) ChipDefaults.primaryChipColors() else ChipDefaults.secondaryChipColors(),
                            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                        )
                    }
                }
            }
        }

        item {
            CompactChip(
                onClick = onBack,
                label = { Text("‹ Volver", fontSize = 11.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            )
        }
    }
}
