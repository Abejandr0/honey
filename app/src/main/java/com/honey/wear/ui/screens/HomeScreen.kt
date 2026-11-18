package com.honey.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Settings
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.honey.wear.R
import com.honey.wear.data.MoodType
import com.honey.wear.data.PartnerState
import com.honey.wear.data.UserProfile
import com.honey.wear.ui.components.HoneyIconTile
import com.honey.wear.ui.components.moodIcon
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    partnerState: PartnerState,
    myMood: MoodType?,
    daysTogether: Long?,
    isPreview: Boolean,
    isDndActive: Boolean,
    onNavigateToTouch: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToLocation: () -> Unit,
    onNavigateToMood: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    val settingsLabel = stringResource(R.string.nav_settings)
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(30_000)
        }
    }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(horizontal = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 3.dp, bottom = 14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isPreview) 10.sp else 12.sp
                    )
                    Text(
                        text = if (isDndActive) stringResource(R.string.status_dnd) else userProfile.username,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.72f),
                        fontSize = if (isPreview) 8.sp else 9.sp,
                        maxLines = 1
                    )
                }
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(if (isPreview) 25.dp else 36.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onNavigateToSettings)
                        .semantics { contentDescription = settingsLabel },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface,
                        modifier = Modifier.size(if (isPreview) 16.dp else 19.dp)
                    )
                }
            }
        }

        if (isPreview) item {
            Text(
                text = stringResource(R.string.preview_banner),
                color = MaterialTheme.colors.primary,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                text = currentTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                color = MaterialTheme.colors.onBackground,
                fontSize = if (isPreview) 27.sp else 34.sp,
                lineHeight = if (isPreview) 29.sp else 38.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }

        if (daysTogether != null) item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.days_together_format, daysTogether),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.82f),
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }

        if (!isPreview) item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    partnerState.mood?.let { mood ->
                        Icon(
                            imageVector = moodIcon(mood),
                            contentDescription = stringResource(mood.labelRes),
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        text = stringResource(
                            R.string.partner_mood_format,
                            partnerState.name,
                            partnerState.mood?.let { stringResource(it.labelRes) } ?: stringResource(R.string.mood_not_set)
                        ),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.78f),
                        fontSize = 9.sp,
                        maxLines = 1
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = myMood?.let(::moodIcon) ?: Icons.Default.Mood,
                        contentDescription = myMood?.let { stringResource(it.labelRes) }
                            ?: stringResource(R.string.mood_not_set),
                        tint = MaterialTheme.colors.primary.copy(alpha = 0.78f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = stringResource(
                            R.string.my_mood_format,
                            myMood?.let { stringResource(it.labelRes) } ?: stringResource(R.string.mood_not_set)
                        ),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                        fontSize = 8.sp,
                        maxLines = 1
                    )
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                HoneyIconTile(
                    label = stringResource(R.string.nav_touch),
                    icon = Icons.Default.Favorite,
                    modifier = Modifier.weight(1f),
                    compact = isPreview,
                    onClick = onNavigateToTouch
                )
                HoneyIconTile(
                    label = stringResource(R.string.nav_messages),
                    icon = Icons.Default.ChatBubbleOutline,
                    modifier = Modifier.weight(1f),
                    compact = isPreview,
                    onClick = onNavigateToMessages
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                HoneyIconTile(
                    label = stringResource(R.string.nav_location),
                    icon = Icons.Default.LocationOn,
                    modifier = Modifier.weight(1f),
                    compact = isPreview,
                    onClick = onNavigateToLocation
                )
                HoneyIconTile(
                    label = stringResource(R.string.nav_mood),
                    icon = Icons.Default.Mood,
                    modifier = Modifier.weight(1f),
                    compact = isPreview,
                    onClick = onNavigateToMood
                )
            }
        }

        item { Spacer(Modifier.height(2.dp)) }
    }
}
