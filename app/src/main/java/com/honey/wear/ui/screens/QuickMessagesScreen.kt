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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.honey.wear.R
import com.honey.wear.ui.components.HoneyBackChip

private data class QuickMessageOption(val messageRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun QuickMessagesScreen(
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit
) {
    val messages = listOf(
        QuickMessageOption(R.string.msg_love, Icons.Default.Favorite),
        QuickMessageOption(R.string.msg_on_my_way, Icons.Default.DirectionsRun),
        QuickMessageOption(R.string.msg_arrived, Icons.Default.LocationOn),
        QuickMessageOption(R.string.msg_thinking, Icons.Default.ChatBubbleOutline),
        QuickMessageOption(R.string.msg_call_me, Icons.Default.Phone),
        QuickMessageOption(R.string.msg_good_night, Icons.Default.Bedtime)
    )

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 5.dp, bottom = 16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.quick_messages_title),
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        messages.forEach { option ->
            item {
                val message = stringResource(option.messageRes)
                Chip(
                    onClick = { onSendMessage(message) },
                    label = { Text(message, fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                    icon = {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(17.dp)
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                )
            }
        }

        item { HoneyBackChip(stringResource(R.string.back), onBack) }
    }
}
