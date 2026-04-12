package com.example.ecoswap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecoswap.data.FirebaseRepository
import com.example.ecoswap.data.UserProfile
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun register(name: String, nis: String, password: String) {
        uiState = uiState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repository.registerSiswa(name, nis, password)
            uiState = if (result.isSuccess) {
                uiState.copy(isLoading = false, success = true, error = null)
            } else {
                uiState.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Gagal daftar")
            }
        }
    }

    fun loginSiswa(nis: String, password: String, onSuccess: (UserProfile) -> Unit) {
        uiState = uiState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repository.loginSiswa(nis, password)
            if (result.isSuccess) {
                uiState = uiState.copy(isLoading = false)
                onSuccess(result.getOrThrow())
            } else {
                uiState = uiState.copy(isLoading = false, error = "NIS atau kata sandi salah")
            }
        }
    }

    fun loginMitra(email: String, password: String, onSuccess: (UserProfile) -> Unit) {
        uiState = uiState.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repository.loginMitra(email, password)
            if (result.isSuccess) {
                uiState = uiState.copy(isLoading = false)
                onSuccess(result.getOrThrow())
            } else {
                uiState = uiState.copy(isLoading = false, error = "Email atau kata sandi salah")
            }
        }
    }

    fun logout() = repository.logout()

    fun clearError() {
        uiState = uiState.copy(error = null)
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)