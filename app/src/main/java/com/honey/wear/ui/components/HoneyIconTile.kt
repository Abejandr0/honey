package com.honey.wear.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ChipDefaults
import com.honey.wear.data.MoodType
import com.honey.wear.data.TouchType

fun moodIcon(mood: MoodType): ImageVector = when (mood) {
    MoodType.HAPPY -> Icons.Default.Face
    MoodType.IN_LOVE -> Icons.Default.Favorite
    MoodType.TIRED -> Icons.Default.AccessTime
    MoodType.CUDDLE -> Icons.Default.FavoriteBorder
    MoodType.STRESSED -> Icons.Default.Warning
    MoodType.PLAYFUL -> Icons.Default.AutoAwesome
}

fun touchIcon(type: TouchType): ImageVector = when (type) {
    TouchType.CARINO -> Icons.Default.Favorite
    TouchType.ABRAZO -> Icons.Default.FavoriteBorder
    TouchType.BESO -> Icons.Default.AutoAwesome
}

@Composable
fun HoneyIconTile(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    compact: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    val colors = MaterialTheme.colors

    Column(
        modifier = modifier
            .padding(if (compact) 2.dp else 4.dp)
            .height(if (compact) 46.dp else 76.dp)
            .clip(shape)
            .background(if (selected) colors.primary.copy(alpha = 0.16f) else colors.surface)
            .then(
                if (selected) Modifier.border(1.5.dp, colors.primary, shape) else Modifier
            )
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 22.dp else 34.dp)
                .clip(CircleShape)
                .background(colors.primary.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(if (compact) 14.dp else 19.dp)
            )
        }
        Spacer(Modifier.height(if (compact) 2.dp else 5.dp))
        Text(
            text = label,
            color = colors.onSurface,
            fontSize = if (compact) 9.sp else 11.sp,
            lineHeight = if (compact) 10.sp else 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun HoneyBackChip(label: String, onClick: () -> Unit) {
    CompactChip(
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp) },
        icon = {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
        },
        colors = ChipDefaults.secondaryChipColors()
    )
}

@Composable
fun HoneyColorSwatch(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(31.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (selected) Modifier.border(2.dp, MaterialTheme.colors.onSurface, CircleShape)
                else Modifier
            )
            .clickable(role = Role.Button, onClick = onClick)
            .semantics {
                contentDescription = label
                role = Role.RadioButton
            },
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (color.luminance() > 0.48f) androidx.compose.ui.graphics.Color.Black
                else androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
