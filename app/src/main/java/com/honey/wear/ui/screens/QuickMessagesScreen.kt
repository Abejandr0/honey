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

data class QuickMsgItem(val resId: Int, val icon: String)

@Composable
fun QuickMessagesScreen(
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit
) {
    val messages = listOf(
        QuickMsgItem(R.string.msg_love, "❤️"),
        QuickMsgItem(R.string.msg_on_my_way, "🏃‍♂️"),
        QuickMsgItem(R.string.msg_arrived, "📍"),
        QuickMsgItem(R.string.msg_thinking, "💭"),
        QuickMsgItem(R.string.msg_call_me, "📞"),
        QuickMsgItem(R.string.msg_good_night, "🌙")
    )

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.quick_messages_title),
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )
        }

        messages.forEach { item ->
            item {
                val text = stringResource(item.resId)
                Chip(
                    onClick = { onSendMessage(text) },
                    label = { Text(text, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Text(item.icon, fontSize = 16.sp) },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                )
            }
        }

        item {
            CompactChip(
                onClick = onBack,
                label = { Text("‹ Volver", fontSize = 11.sp) },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            )
        }
    }
}
