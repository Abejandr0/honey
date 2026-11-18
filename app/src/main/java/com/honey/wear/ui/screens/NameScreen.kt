package com.honey.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.honey.wear.R
import com.honey.wear.ui.components.HoneyTextField

@Composable
fun NameScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onContinue: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val colors = MaterialTheme.colors

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().background(colors.background).padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 10.dp, bottom = 18.dp)
    ) {
        item {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(25.dp)
            )
        }
        item {
            Text(
                text = stringResource(R.string.name_screen_title),
                color = colors.onBackground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        item {
            Text(
                text = stringResource(R.string.name_screen_hint),
                color = colors.onSurface.copy(alpha = 0.7f),
                fontSize = 9.sp,
                textAlign = TextAlign.Center
            )
        }
        item {
            HoneyTextField(
                value = name,
                onValueChange = { name = it.take(40) },
                label = stringResource(R.string.name_label),
                imeAction = ImeAction.Done
            )
        }
        if (errorMessage != null) {
            item {
                Text(
                    text = errorMessage,
                    color = colors.error,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        item {
            Button(
                onClick = { onContinue(name.trim()) },
                enabled = !isLoading && name.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Text(
                    text = stringResource(if (isLoading) R.string.loading else R.string.btn_complete_setup),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
