package com.honey.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.honey.wear.R
import com.honey.wear.ui.components.HoneyTextField
import java.time.LocalDate

@Composable
fun SetupScreen(
    pairingCode: String?,
    isGeneratingCode: Boolean,
    isPairing: Boolean,
    errorMessage: String?,
    onCreatePairingCode: () -> Unit,
    onPairWithCode: (code: String, anniversaryDate: LocalDate?) -> Unit
) {
    var partnerCode by remember { mutableStateOf("") }
    var anniversaryText by remember { mutableStateOf("") }
    var copiedCode by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val colors = MaterialTheme.colors

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 8.dp, bottom = 18.dp)
    ) {
        item {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(22.dp)
            )
        }
        item {
            Text(
                text = stringResource(R.string.setup_partner_match),
                color = colors.onBackground,
                style = MaterialTheme.typography.title3.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }
        item {
            Text(
                text = stringResource(R.string.pairing_waiting),
                color = colors.onSurface.copy(alpha = 0.7f),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }

        item {
            Text(
                text = stringResource(R.string.my_code_label),
                color = colors.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
        if (pairingCode.isNullOrBlank()) {
            item {
                Button(
                    onClick = onCreatePairingCode,
                    enabled = !isGeneratingCode,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = ButtonDefaults.primaryButtonColors()
                ) {
                    Text(
                        text = stringResource(if (isGeneratingCode) R.string.loading else R.string.create_pairing_code),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pairingCode,
                        color = colors.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Button(
                        onClick = {
                            clipboard.setText(AnnotatedString(pairingCode))
                            copiedCode = true
                        },
                        modifier = Modifier.padding(start = 5.dp).size(36.dp),
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.copy_code),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            item {
                Text(
                    text = stringResource(R.string.pairing_code_expires),
                    color = colors.onSurface.copy(alpha = 0.58f),
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center
                )
            }
            if (copiedCode) {
                item {
                    Text(
                        text = stringResource(R.string.code_copied),
                        color = colors.primary,
                        fontSize = 8.sp
                    )
                }
            }
            item {
                Button(
                    onClick = onCreatePairingCode,
                    enabled = !isGeneratingCode,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Text(stringResource(R.string.refresh_pairing_code), fontSize = 9.sp)
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.or_enter_partner_code),
                color = colors.onSurface.copy(alpha = 0.62f),
                fontSize = 9.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        item {
            HoneyTextField(
                value = partnerCode,
                onValueChange = { partnerCode = it.uppercase().filter(Char::isLetterOrDigit).take(13) },
                label = stringResource(R.string.pairing_code_label),
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Next
            )
        }
        item {
            HoneyTextField(
                value = anniversaryText,
                onValueChange = { anniversaryText = it.take(10) },
                label = stringResource(R.string.anniversary_optional),
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        }

        if (errorMessage != null) {
            item {
                Text(
                    text = errorMessage,
                    color = colors.error,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 3.dp)
                )
            }
        }

        item {
            Button(
                onClick = {
                    val date = anniversaryText.takeIf { it.isNotBlank() }?.let {
                        runCatching { LocalDate.parse(it) }.getOrNull()
                    }
                    onPairWithCode(partnerCode.trim(), date)
                },
                enabled = !isPairing && partnerCode.length == 13 &&
                    (anniversaryText.isBlank() || runCatching { LocalDate.parse(anniversaryText) }.isSuccess),
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Text(
                    text = stringResource(if (isPairing) R.string.loading else R.string.pair_action),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }
}
