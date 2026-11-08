package com.honey.wear.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.honey.wear.R
import com.honey.wear.data.TouchType

@Composable
fun TouchReactionOverlay(
    touchType: TouchType,
    onDismiss: () -> Unit
) {
    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "touchPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val gradient = when (touchType) {
        TouchType.CARINO -> Brush.radialGradient(
            listOf(Color(0xFFF43F5E), Color(0xFF881337))
        )
        TouchType.ABRAZO -> Brush.radialGradient(
            listOf(Color(0xFFF59E0B), Color(0xFF7C2D12))
        )
        TouchType.BESO -> Brush.radialGradient(
            listOf(Color(0xFFD946EF), Color(0xFF581C87))
        )
    }

    val titleRes = when (touchType) {
        TouchType.CARINO -> R.string.touch_received_carino
        TouchType.ABRAZO -> R.string.touch_received_abrazo
        TouchType.BESO -> R.string.touch_received_beso
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .clickable { onDismiss() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = touchType.emoji,
                fontSize = 54.sp,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.ExtraBold),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Toca para cerrar",
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
