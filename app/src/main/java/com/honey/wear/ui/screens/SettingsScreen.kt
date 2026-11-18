package com.honey.wear.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Vibration
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.SwitchDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import com.honey.wear.R
import com.honey.wear.data.HoneySettings
import com.honey.wear.ui.components.HoneyBackChip
import com.honey.wear.ui.components.HoneyColorSwatch
import com.honey.wear.ui.theme.HoneyThemePalette

@Composable
fun SettingsScreen(
    settings: HoneySettings,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleDnd: (Boolean) -> Unit,
    onToggleVibration: (Boolean) -> Unit,
    onSelectTheme: (HoneyThemePalette) -> Unit,
    onToggleLanguage: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.settings_title),
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        item {
            Chip(
                onClick = onToggleLanguage,
                label = { Text(stringResource(R.string.pref_language), fontSize = 11.sp) },
                secondaryLabel = {
                    Text(
                        stringResource(if (settings.languageCode == "es") R.string.language_spanish else R.string.language_english),
                        fontSize = 9.sp
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.size(17.dp)
                    )
                },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        item {
            ToggleChip(
                checked = settings.isDarkMode,
                onCheckedChange = onToggleDarkMode,
                label = { Text(stringResource(R.string.pref_dark_mode), fontSize = 11.sp) },
                appIcon = {
                    Icon(
                        imageVector = if (settings.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.size(17.dp)
                    )
                },
                toggleControl = {
                    Switch(
                        checked = settings.isDarkMode,
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary)
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        item {
            ToggleChip(
                checked = settings.isDndActive,
                onCheckedChange = onToggleDnd,
                label = { Text(stringResource(R.string.pref_dnd), fontSize = 11.sp) },
                secondaryLabel = { Text(stringResource(R.string.pref_dnd_desc), fontSize = 8.sp) },
                appIcon = {
                    Icon(
                        imageVector = Icons.Default.DoNotDisturbOn,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.size(17.dp)
                    )
                },
                toggleControl = {
                    Switch(
                        checked = settings.isDndActive,
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary)
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        item {
            ToggleChip(
                checked = settings.isVibrationEnabled,
                onCheckedChange = onToggleVibration,
                label = { Text(stringResource(R.string.pref_vibration), fontSize = 11.sp) },
                secondaryLabel = { Text(stringResource(R.string.pref_vibration_desc), fontSize = 8.sp) },
                appIcon = {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.size(17.dp)
                    )
                },
                toggleControl = {
                    Switch(
                        checked = settings.isVibrationEnabled,
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary)
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        item {
            Text(
                text = stringResource(R.string.pref_theme),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.78f),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
            )
        }

        HoneyThemePalette.entries.chunked(4).forEach { rowThemes ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowThemes.forEach { theme ->
                        HoneyColorSwatch(
                            label = stringResource(theme.labelRes),
                            color = theme.primary,
                            selected = settings.themePalette == theme,
                            onClick = { onSelectTheme(theme) }
                        )
                    }
                }
            }
        }

        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(settings.themePalette.labelRes),
                    color = MaterialTheme.colors.primary,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                )
                Chip(
                    onClick = onLogout,
                    label = { Text(stringResource(R.string.pref_logout), fontSize = 10.sp) },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                )
                HoneyBackChip(stringResource(R.string.back), onBack)
            }
        }
    }
}
