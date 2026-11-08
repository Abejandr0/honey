package com.honey.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.honey.wear.R
import com.honey.wear.data.MoodType
import com.honey.wear.data.PartnerState
import com.honey.wear.data.UserProfile
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    partnerState: PartnerState,
    myMood: MoodType,
    daysTogether: Long,
    isDndActive: Boolean,
    onNavigateToTouch: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToLocation: () -> Unit,
    onNavigateToMood: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    // Dynamic real-time clock ticker
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(1000)
        }
    }

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val secondsFormatter = DateTimeFormatter.ofPattern("ss")

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Top Row: DND badge & Sync dot
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Partner Avatar + Sync
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${userProfile.avatarEmoji}💞${partnerState.avatarEmoji}", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colors.primary)
                    )
                }

                if (isDndActive) {
                    Text(text = "🌙 DND", fontSize = 10.sp, color = MaterialTheme.colors.primary)
                } else {
                    Text(text = "100% ⚡", fontSize = 10.sp, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
                }
            }
        }

        // Digital Clock with seconds
        item {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(
                    text = currentTime.format(timeFormatter),
                    style = MaterialTheme.typography.display1.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    ),
                    color = MaterialTheme.colors.onBackground
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = currentTime.format(secondsFormatter),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }

        // Days Together Counter Pill
        item {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "💞 " + stringResource(R.string.days_together_format, daysTogether),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
            }
        }

        // Mutual Mood Bar
        item {
            Card(
                onClick = onNavigateToMood,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${partnerState.name}: ${partnerState.currentMood.emoji}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tú: ${myMood.emoji} (Tocar para cambiar)",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Text(text = "›", fontSize = 16.sp, color = MaterialTheme.colors.primary)
                }
            }
        }

        // Navigation Chips: Cariño & Mensajes
        item {
            Chip(
                onClick = onNavigateToTouch,
                label = { Text(stringResource(R.string.nav_touch), fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Text("💗", fontSize = 16.sp) },
                colors = ChipDefaults.primaryChipColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        item {
            Chip(
                onClick = onNavigateToMessages,
                label = { Text(stringResource(R.string.nav_messages), fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Text("💬", fontSize = 16.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        item {
            Chip(
                onClick = onNavigateToLocation,
                label = { Text(stringResource(R.string.nav_location), fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Text("📍", fontSize = 16.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        item {
            Chip(
                onClick = onNavigateToMood,
                label = { Text(stringResource(R.string.nav_mood), fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Text("🙂", fontSize = 16.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        item {
            CompactChip(
                onClick = onNavigateToSettings,
                label = { Text("⚙️ " + stringResource(R.string.nav_settings), fontSize = 11.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
        }
    }
}
