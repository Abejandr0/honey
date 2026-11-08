package com.honey.wear.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.honey.wear.R
import com.honey.wear.data.TouchType

@Composable
fun TouchScreen(
    onSendTouch: (TouchType) -> Unit,
    onSimulateIncoming: (TouchType) -> Unit,
    onBack: () -> Unit
) {
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.touch_title),
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            Text(
                text = stringResource(R.string.touch_subtitle),
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        // Mode 1: Cariño (Heartbeat pulse)
        item {
            Chip(
                onClick = { onSendTouch(TouchType.CARINO) },
                label = { Text(stringResource(R.string.mode_carino), fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                secondaryLabel = { Text(stringResource(R.string.desc_carino), fontSize = 9.sp) },
                icon = { Text("💗", fontSize = 22.sp) },
                colors = ChipDefaults.primaryChipColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
            )
        }

        // Mode 2: Abrazo (Warm embrace)
        item {
            Chip(
                onClick = { onSendTouch(TouchType.ABRAZO) },
                label = { Text(stringResource(R.string.mode_abrazo), fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                secondaryLabel = { Text(stringResource(R.string.desc_abrazo), fontSize = 9.sp) },
                icon = { Text("🤗", fontSize = 22.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
            )
        }

        // Mode 3: Beso (Sweet Kiss)
        item {
            Chip(
                onClick = { onSendTouch(TouchType.BESO) },
                label = { Text(stringResource(R.string.mode_beso), fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                secondaryLabel = { Text(stringResource(R.string.desc_beso), fontSize = 9.sp) },
                icon = { Text("😘", fontSize = 22.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
            )
        }

        // Test Simulator Section: Receive touch on this watch
        item {
            Text(
                text = "⚡ Probar recibir en este reloj:",
                style = MaterialTheme.typography.caption3,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CompactChip(
                    onClick = { onSimulateIncoming(TouchType.CARINO) },
                    label = { Text("Recibir 💗", fontSize = 10.sp) },
                    colors = ChipDefaults.secondaryChipColors()
                )
                CompactChip(
                    onClick = { onSimulateIncoming(TouchType.BESO) },
                    label = { Text("Recibir 😘", fontSize = 10.sp) },
                    colors = ChipDefaults.secondaryChipColors()
                )
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
