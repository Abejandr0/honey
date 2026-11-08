package com.honey.wear.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.honey.wear.R
import com.honey.wear.data.HoneySettings
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
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }

        // Language Switcher (ES / EN)
        item {
            Chip(
                onClick = onToggleLanguage,
                label = { Text(stringResource(R.string.pref_language), fontSize = 11.5.sp) },
                secondaryLabel = { Text(if (settings.languageCode == "es") "Español 🇪🇸" else "English 🇺🇸", fontSize = 10.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        // OLED Dark Mode Toggle
        item {
            ToggleChip(
                checked = settings.isDarkMode,
                onCheckedChange = { onToggleDarkMode(it) },
                label = { Text(stringResource(R.string.pref_dark_mode), fontSize = 11.5.sp) },
                toggleControl = {
                    Switch(
                        checked = settings.isDarkMode,
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary)
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        // Do Not Disturb (DND) Toggle
        item {
            ToggleChip(
                checked = settings.isDndActive,
                onCheckedChange = { onToggleDnd(it) },
                label = { Text(stringResource(R.string.pref_dnd), fontSize = 11.5.sp) },
                secondaryLabel = { Text(stringResource(R.string.pref_dnd_desc), fontSize = 8.5.sp) },
                toggleControl = {
                    Switch(
                        checked = settings.isDndActive,
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary)
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        // Haptic Vibration Hardware Switch
        item {
            ToggleChip(
                checked = settings.isVibrationEnabled,
                onCheckedChange = { onToggleVibration(it) },
                label = { Text(stringResource(R.string.pref_vibration), fontSize = 11.5.sp) },
                secondaryLabel = { Text(stringResource(R.string.pref_vibration_desc), fontSize = 8.5.sp) },
                toggleControl = {
                    Switch(
                        checked = settings.isVibrationEnabled,
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary)
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }

        // 8 Color Themes Section
        item {
            Text(
                text = "🎨 " + stringResource(R.string.pref_theme),
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Theme palette chips (Row 1)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(HoneyThemePalette.HONEY, HoneyThemePalette.BLUE, HoneyThemePalette.PINK, HoneyThemePalette.PURPLE).forEach { th ->
                    CompactChip(
                        onClick = { onSelectTheme(th) },
                        label = { Text(th.displayName, fontSize = 9.sp) },
                        colors = if (settings.themePalette == th) ChipDefaults.primaryChipColors() else ChipDefaults.secondaryChipColors()
                    )
                }
            }
        }

        // Theme palette chips (Row 2: Verde, Coral, Neón, Rubí)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(HoneyThemePalette.GREEN, HoneyThemePalette.CORAL, HoneyThemePalette.CYAN, HoneyThemePalette.RUBY).forEach { th ->
                    CompactChip(
                        onClick = { onSelectTheme(th) },
                        label = { Text(th.displayName, fontSize = 9.sp) },
                        colors = if (settings.themePalette == th) ChipDefaults.primaryChipColors() else ChipDefaults.secondaryChipColors()
                    )
                }
            }
        }

        // Sign Out / Unlink
        item {
            CompactChip(
                onClick = onLogout,
                label = { Text(stringResource(R.string.pref_logout), fontSize = 10.sp, color = MaterialTheme.colors.error) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Back button
        item {
            CompactChip(
                onClick = onBack,
                label = { Text("‹ Volver", fontSize = 11.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
        }
    }
}
