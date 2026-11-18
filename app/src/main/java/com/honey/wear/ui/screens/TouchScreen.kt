package com.honey.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.honey.wear.R
import com.honey.wear.data.TouchType
import com.honey.wear.ui.components.HoneyBackChip
import com.honey.wear.ui.components.touchIcon

@Composable
fun TouchScreen(
    onSendTouch: (TouchType) -> Unit,
    onBack: () -> Unit
) {
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 6.dp, bottom = 16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.touch_title),
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.Bold)
            )
        }
        item {
            Text(
                text = stringResource(R.string.touch_subtitle),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }

        TouchType.entries.forEachIndexed { index, type ->
            item {
                Chip(
                    onClick = { onSendTouch(type) },
                    label = {
                        Text(
                            text = stringResource(type.titleRes),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    },
                    secondaryLabel = { Text(stringResource(type.descRes), fontSize = 8.sp) },
                    icon = {
                        Icon(
                            imageVector = touchIcon(type),
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = if (index == 0) ChipDefaults.primaryChipColors() else ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                )
            }
        }

        item { HoneyBackChip(stringResource(R.string.back), onBack) }
    }
}
