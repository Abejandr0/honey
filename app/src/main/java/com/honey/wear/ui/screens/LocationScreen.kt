package com.honey.wear.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.honey.wear.R
import com.honey.wear.data.PartnerState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LocationScreen(
    isSharingLocation: Boolean,
    distanceMeters: Int,
    partnerState: PartnerState,
    onToggleSharing: (Boolean) -> Unit,
    onRefreshLocation: () -> Unit,
    onBack: () -> Unit
) {
    // Radar sweep rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "radarSweep")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.location_title),
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Radar Canvas display
        item {
            val radarColor = MaterialTheme.colors.primary
            Canvas(
                modifier = Modifier
                    .size(90.dp)
                    .padding(4.dp)
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2

                // Concentric circles
                drawCircle(color = radarColor.copy(alpha = 0.2f), radius = radius, style = Stroke(width = 2f))
                drawCircle(color = radarColor.copy(alpha = 0.35f), radius = radius * 0.65f, style = Stroke(width = 2f))
                drawCircle(color = radarColor.copy(alpha = 0.5f), radius = radius * 0.3f, style = Stroke(width = 2f))

                // Radar sweep line
                val rad = Math.toRadians(angle.toDouble())
                val endOffset = Offset(
                    (center.x + radius * cos(rad)).toFloat(),
                    (center.y + radius * sin(rad)).toFloat()
                )
                drawLine(
                    color = radarColor.copy(alpha = 0.8f),
                    start = center,
                    end = endOffset,
                    strokeWidth = 3f
                )

                // My position (Center dot)
                drawCircle(color = Color.White, radius = 5f, center = center)

                // Partner position (Offset beacon)
                if (partnerState.isSharingLocation) {
                    val partnerOffset = Offset(center.x + radius * 0.45f, center.y - radius * 0.35f)
                    drawCircle(color = radarColor, radius = 6f, center = partnerOffset)
                }
            }
        }

        // Distance text
        item {
            val distString = if (distanceMeters < 1000) "$distanceMeters m" else String.format("%.1f km", distanceMeters / 1000f)
            Text(
                text = if (partnerState.isSharingLocation) {
                    "📍 " + stringResource(R.string.distance_format, distString)
                } else {
                    stringResource(R.string.partner_not_sharing)
                },
                style = MaterialTheme.typography.caption1.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface
            )
        }

        // Toggle Share switch chip
        item {
            ToggleChip(
                checked = isSharingLocation,
                onCheckedChange = { onToggleSharing(it) },
                label = { Text(stringResource(R.string.share_location_switch), fontSize = 11.sp) },
                toggleControl = {
                    Switch(
                        checked = isSharingLocation,
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary)
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
        }

        // Refresh GPS Chip
        item {
            CompactChip(
                onClick = onRefreshLocation,
                label = { Text("🔄 " + stringResource(R.string.btn_refresh_gps), fontSize = 10.sp) },
                colors = ChipDefaults.secondaryChipColors()
            )
        }

        item {
            CompactChip(
                onClick = onBack,
                label = { Text("‹ Volver", fontSize = 11.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
            )
        }
    }
}
