package com.app.instagramclone.ui.screen.auth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.instagramclone.presentation.auth.AuthUiState
import com.app.instagramclone.presentation.auth.AuthViewModel
import com.app.instagramclone.ui.theme.InstagramCloneTheme

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var username by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    LoginScreenContent(
        username = username,
        uiState = uiState,
        onUsernameChange = { username = it },
        onLoginClick = { viewModel.login(username) }
    )
}

@Composable
fun LoginScreenContent(
    username: String,
    uiState: AuthUiState,
    onUsernameChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Instagram",
            fontSize = 42.sp,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.Cursive,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Nome de usuário") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState is AuthUiState.Error) {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onLoginClick,
            enabled = uiState !is AuthUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Entrar")
            }
        }
    }
}

@Preview(name = "Login — Light", showBackground = true)
@Preview(name = "Login — Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenIdlePreview() {
    InstagramCloneTheme {
        Surface {
            LoginScreenContent(
                username = "",
                uiState = AuthUiState.Idle,
                onUsernameChange = {},
                onLoginClick = {}
            )
        }
    }
}

@Preview(name = "Login — Carregando", showBackground = true)
@Composable
private fun LoginScreenLoadingPreview() {
    InstagramCloneTheme {
        Surface {
            LoginScreenContent(
                username = "pether",
                uiState = AuthUiState.Loading,
                onUsernameChange = {},
                onLoginClick = {}
            )
        }
    }
}

@Preview(name = "Login — Erro", showBackground = true)
@Composable
private fun LoginScreenErrorPreview() {
    InstagramCloneTheme {
        Surface {
            LoginScreenContent(
                username = "pether",
                uiState = AuthUiState.Error("Usuário não encontrado"),
                onUsernameChange = {},
                onLoginClick = {}
            )
        }
    }
}
