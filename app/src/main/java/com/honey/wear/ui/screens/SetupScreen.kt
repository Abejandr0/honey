package com.honey.wear.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.honey.wear.R
import java.time.LocalDate

@Composable
fun SetupScreen(
    myPairingCode: String,
    onSetupCompleted: (partnerCode: String, anniversary: LocalDate, avatar: String) -> Unit
) {
    val avatars = listOf("🌸", "🐻", "🐱", "🦊", "🌙", "🐝")
    var selectedAvatarIndex by remember { mutableStateOf(0) }
    var partnerCode by remember { mutableStateOf("HNY-5521") }

    // Anniversary state
    var day by remember { mutableStateOf(14) }
    var month by remember { mutableStateOf(2) }
    var year by remember { mutableStateOf(2024) }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.setup_title),
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        // Avatar Picker
        item {
            Text(
                text = "Elige tu avatar:",
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                avatars.take(3).forEachIndexed { idx, emo ->
                    CompactChip(
                        onClick = { selectedAvatarIndex = idx },
                        label = { Text(emo, fontSize = 16.sp) },
                        colors = if (selectedAvatarIndex == idx) ChipDefaults.primaryChipColors() else ChipDefaults.secondaryChipColors()
                    )
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                avatars.drop(3).forEachIndexed { idx, emo ->
                    val actualIdx = idx + 3
                    CompactChip(
                        onClick = { selectedAvatarIndex = actualIdx },
                        label = { Text(emo, fontSize = 16.sp) },
                        colors = if (selectedAvatarIndex == actualIdx) ChipDefaults.primaryChipColors() else ChipDefaults.secondaryChipColors()
                    )
                }
            }
        }

        // My Pairing Code
        item {
            Card(
                onClick = {},
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.my_code_label),
                        style = MaterialTheme.typography.caption3,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = myPairingCode,
                        style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colors.primary,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        // Partner Code Match Chip
        item {
            Chip(
                onClick = {
                    partnerCode = if (partnerCode == "HNY-5521") "HNY-9901" else "HNY-5521"
                },
                label = { Text("Pareja: $partnerCode", fontSize = 12.sp) },
                secondaryLabel = { Text("Tocar para vincular", fontSize = 10.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Anniversary Date Section
        item {
            Text(
                text = stringResource(R.string.anniversary_date_label),
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Day adjuster
                CompactChip(
                    onClick = { day = if (day >= 31) 1 else day + 1 },
                    label = { Text("Día: $day", fontSize = 11.sp) },
                    colors = ChipDefaults.secondaryChipColors()
                )
                // Month adjuster
                CompactChip(
                    onClick = { month = if (month >= 12) 1 else month + 1 },
                    label = { Text("Mes: $month", fontSize = 11.sp) },
                    colors = ChipDefaults.secondaryChipColors()
                )
                // Year adjuster
                CompactChip(
                    onClick = { year = if (year >= 2026) 2020 else year + 1 },
                    label = { Text("$year", fontSize = 11.sp) },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
        }

        // Finish Setup Button
        item {
            Button(
                onClick = {
                    val date = try {
                        LocalDate.of(year, month, day)
                    } catch (e: Exception) {
                        LocalDate.of(2024, 2, 14)
                    }
                    onSetupCompleted(partnerCode, date, avatars[selectedAvatarIndex])
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Text(
                    text = stringResource(R.string.btn_complete_setup),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
