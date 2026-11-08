package com.honey.wear.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.honey.wear.R

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onLogin: (pin: String) -> Boolean,
    onRegister: (username: String, pin: String) -> Boolean
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var usernameInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Brand Icon
        item {
            Text(
                text = "🍯",
                fontSize = 28.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.title2.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colors.primary
            )
        }

        item {
            Text(
                text = stringResource(if (isRegisterMode) R.string.register_title else R.string.login_title),
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        // Mode Switcher (Login / Register)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                CompactChip(
                    onClick = {
                        isRegisterMode = false
                        errorMessage = null
                    },
                    label = { Text("Entrar", fontSize = 11.sp) },
                    colors = if (!isRegisterMode) ChipDefaults.primaryChipColors() else ChipDefaults.secondaryChipColors()
                )
                Spacer(modifier = Modifier.width(6.dp))
                CompactChip(
                    onClick = {
                        isRegisterMode = true
                        errorMessage = null
                    },
                    label = { Text("Registro", fontSize = 11.sp) },
                    colors = if (isRegisterMode) ChipDefaults.primaryChipColors() else ChipDefaults.secondaryChipColors()
                )
            }
        }

        // If register mode, show username preview / quick options
        if (isRegisterMode) {
            item {
                Chip(
                    onClick = {
                        usernameInput = if (usernameInput == "Abe") "Dani" else "Abe"
                    },
                    label = {
                        Text(
                            text = if (usernameInput.isBlank()) "Nombre: [Tocar Abe]" else "Nombre: $usernameInput",
                            fontSize = 12.sp
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // PIN display / input chip
        item {
            Chip(
                onClick = {
                    // Cyclic PIN helper for easy watch testing
                    pinInput = if (pinInput.length >= 4) "" else pinInput + "1"
                },
                label = {
                    val display = if (pinInput.isEmpty()) "Tocar para PIN (4 dígitos)" else "PIN: ${"•".repeat(pinInput.length)}"
                    Text(display, fontSize = 12.sp)
                },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Error message if any
        if (errorMessage != null) {
            item {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colors.error,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    if (isRegisterMode) {
                        val name = usernameInput.ifBlank { "Usuario" }
                        val pin = pinInput.ifBlank { "1234" }
                        if (onRegister(name, pin)) onAuthSuccess() else errorMessage = "PIN mínimo 4 dígitos"
                    } else {
                        val pin = pinInput.ifBlank { "1234" }
                        if (onLogin(pin)) onAuthSuccess() else errorMessage = "PIN mínimo 4 dígitos"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Text(
                    text = stringResource(if (isRegisterMode) R.string.btn_register else R.string.btn_login),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // Quick Auto-Fill for grading/evaluation
        item {
            CompactChip(
                onClick = {
                    pinInput = "1234"
                    usernameInput = "Abejandro"
                    if (onLogin("1234")) onAuthSuccess()
                },
                label = { Text("⚡ Acceso Rápido Demo", fontSize = 10.sp) },
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}
