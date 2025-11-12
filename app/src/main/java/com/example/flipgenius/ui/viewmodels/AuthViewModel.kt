package com.example.flipgenius.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    // Estado do Login
    val loginUser: String = "",
    val loginPassword: String = "",
    val loginSelectedTabIndex: Int = 0,

    val registerUser: String = "",
    val registerPassword: String = "",
    val registerConfirmPassword: String = "",
    
    // Usuário logado
    val usuarioId: Long? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()
    
    // Helper para obter o valor atual do usuarioId
    fun getUsuarioId(): Long? = _uiState.value.usuarioId

    fun onLoginTabChange(index: Int) {
        _uiState.update { it.copy(loginSelectedTabIndex = index) }
    }
    fun onLoginUserChange(user: String) {
        _uiState.update { it.copy(loginUser = user) }
    }
    fun onLoginPasswordChange(password: String) {
        _uiState.update { it.copy(loginPassword = password) }
    }

    fun onRegisterUserChange(user: String) {
        _uiState.update { it.copy(registerUser = user) }
    }
    fun onRegisterPasswordChange(password: String) {
        _uiState.update { it.copy(registerPassword = password) }
    }
    fun onRegisterConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(registerConfirmPassword = password) }
    }
    
    fun setUsuarioLogado(id: Long) {
        _uiState.update { it.copy(usuarioId = id) }
    }
    
    fun logout() {
        _uiState.update { it.copy(usuarioId = null) }
    }
}