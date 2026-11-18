package com.honey.wear.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.honey.wear.R
import com.honey.wear.data.TouchType
import androidx.wear.compose.material.Icon

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

    val gradient = Brush.radialGradient(
        listOf(MaterialTheme.colors.primary.copy(alpha = 0.32f), MaterialTheme.colors.background)
    )
    val icon = when (touchType) {
        TouchType.CARINO -> Icons.Default.Favorite
        TouchType.ABRAZO -> Icons.Default.FavoriteBorder
        TouchType.BESO -> Icons.Default.AutoAwesome
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
                text = stringResource(titleRes),
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.dismiss),
                fontSize = 9.sp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.72f)
            )
        }
    }
}
