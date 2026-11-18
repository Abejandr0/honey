package com.honey.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
fun AuthScreen(
    isFirebaseConfigured: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onLogin: (email: String, password: String) -> Unit,
    onRegister: (name: String, email: String, password: String) -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val colors = MaterialTheme.colors

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 10.dp, bottom = 18.dp)
    ) {
        item {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.padding(bottom = 3.dp).height(25.dp)
            )
        }
        item {
            Text(
                text = stringResource(R.string.app_name),
                color = colors.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        item {
            Text(
                text = stringResource(if (isRegisterMode) R.string.register_title else R.string.login_title),
                color = colors.onSurface.copy(alpha = 0.68f),
                fontSize = 10.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (!isFirebaseConfigured) {
            item {
                Text(
                    text = stringResource(R.string.firebase_setup_required),
                    color = colors.error,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }

        if (isRegisterMode) {
            item {
                HoneyTextField(
                    value = name,
                    onValueChange = { name = it.take(40) },
                    label = stringResource(R.string.name_label),
                )
            }
        }
        item {
            HoneyTextField(
                value = email,
                onValueChange = { email = it.trim().take(254) },
                label = stringResource(R.string.email_label),
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next
            )
        }
        item {
            HoneyTextField(
                value = password,
                onValueChange = { password = it.take(128) },
                label = stringResource(R.string.password_label),
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                secret = true
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
                    if (isRegisterMode) onRegister(name.trim(), email.trim(), password)
                    else onLogin(email.trim(), password)
                },
                enabled = isFirebaseConfigured && !isLoading && email.isNotBlank() && password.isNotEmpty() &&
                    (!isRegisterMode || name.isNotBlank()),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                colors = ButtonDefaults.primaryButtonColors()
            ) {
                Text(
                    text = stringResource(if (isLoading) R.string.loading else if (isRegisterMode) R.string.btn_register else R.string.btn_login),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        item {
            Button(
                onClick = { isRegisterMode = !isRegisterMode },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                colors = ButtonDefaults.secondaryButtonColors()
            ) {
                Text(
                    text = stringResource(if (isRegisterMode) R.string.have_account else R.string.need_account),
                    fontSize = 9.sp
                )
            }
        }
    }
}
