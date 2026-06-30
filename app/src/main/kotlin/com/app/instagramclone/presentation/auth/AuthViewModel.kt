package com.app.instagramclone.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.instagramclone.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha usuário e senha")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.login(username, password)
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Erro ao fazer login") }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
