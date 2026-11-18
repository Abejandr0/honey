package com.honey.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.SwitchDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import com.honey.wear.R
import com.honey.wear.data.PartnerState
import com.honey.wear.data.SharedLocation
import com.honey.wear.ui.components.HoneyBackChip
import com.honey.wear.ui.components.PartnerMapView
import java.util.Date
import java.util.Locale

@Composable
fun LocationScreen(
    isSharingLocation: Boolean,
    partnerState: PartnerState,
    partnerLocation: SharedLocation?,
    distanceMeters: Int?,
    darkMode: Boolean,
    languageCode: String,
    allowSharing: Boolean,
    onToggleSharing: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val colors = MaterialTheme.colors
    val lastUpdated = remember(partnerLocation?.updatedAtMillis, languageCode) {
        partnerLocation?.updatedAtMillis
            ?.takeIf { it > 0L }
            ?.let {
                java.text.DateFormat.getTimeInstance(
                    java.text.DateFormat.SHORT,
                    Locale.forLanguageTag(languageCode)
                ).format(Date(it))
            }
    }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 5.dp, bottom = 15.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.location_title),
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.Bold),
                color = colors.onBackground,
                textAlign = TextAlign.Center
            )
        }
        item {
            Text(
                text = partnerState.name,
                color = colors.primary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        item {
            Box(
                modifier = Modifier
                    .size(148.dp)
                    .clip(CircleShape)
                    .border(2.dp, colors.primary.copy(alpha = 0.65f), CircleShape)
                    .background(colors.surface),
                contentAlignment = Alignment.Center
            ) {
                when {
                    !allowSharing -> LocationEmptyState(
                        iconDescription = stringResource(R.string.preview_partner),
                        label = stringResource(R.string.preview_partner),
                        icon = Icons.Default.LocationOff
                    )
                    !partnerState.isSharingLocation -> LocationEmptyState(
                        iconDescription = stringResource(R.string.partner_not_sharing),
                        label = stringResource(R.string.partner_not_sharing),
                        icon = Icons.Default.LocationOff
                    )
                    partnerLocation == null -> LocationEmptyState(
                        iconDescription = stringResource(R.string.location_waiting),
                        label = stringResource(R.string.location_waiting),
                        icon = Icons.Default.MyLocation
                    )
                    else -> PartnerMapView(
                        location = partnerLocation,
                        partnerName = partnerState.name,
                        isDarkTheme = darkMode,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        item {
            Text(
                text = if (!allowSharing) {
                    stringResource(R.string.preview_partner)
                } else if (!partnerState.isSharingLocation) {
                    stringResource(R.string.partner_not_sharing)
                } else if (distanceMeters != null) {
                    val distance = if (distanceMeters < 1000) {
                        stringResource(R.string.distance_meters, distanceMeters)
                    } else {
                        stringResource(R.string.distance_kilometers, distanceMeters / 1000f)
                    }
                    stringResource(R.string.distance_format, distance)
                } else {
                    stringResource(R.string.location_waiting)
                },
                color = colors.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        if (lastUpdated != null && partnerState.isSharingLocation) item {
            Text(
                text = stringResource(R.string.location_updated, lastUpdated),
                color = colors.onSurface.copy(alpha = 0.62f),
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }
        if (partnerState.isSharingLocation && partnerLocation != null) item {
            Text(
                text = stringResource(R.string.map_attribution),
                color = colors.onSurface.copy(alpha = 0.62f),
                fontSize = 7.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }
        if (!allowSharing) item {
            Text(
                text = stringResource(R.string.preview_location_unavailable),
                color = colors.onSurface.copy(alpha = 0.68f),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
        if (allowSharing) item {
            ToggleChip(
                checked = isSharingLocation,
                onCheckedChange = onToggleSharing,
                label = { Text(stringResource(R.string.share_location_switch), fontSize = 10.sp) },
                secondaryLabel = {
                    Text(
                        stringResource(if (isSharingLocation) R.string.sharing_active else R.string.sharing_inactive),
                        fontSize = 8.sp
                    )
                },
                appIcon = {
                    Icon(
                        imageVector = if (isSharingLocation) Icons.Default.LocationOn else Icons.Default.LocationOff,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(17.dp)
                    )
                },
                toggleControl = {
                    Switch(
                        checked = isSharingLocation,
                        colors = SwitchDefaults.colors(checkedThumbColor = colors.primary)
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
        }
        item { HoneyBackChip(stringResource(R.string.back), onBack) }
    }
}

@Composable
private fun LocationEmptyState(iconDescription: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        modifier = Modifier.padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconDescription,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(27.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.75f),
            fontSize = 9.sp,
            lineHeight = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}
