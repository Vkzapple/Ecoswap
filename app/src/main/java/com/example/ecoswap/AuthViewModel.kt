package com.example.ecoswap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun register(
        name: String,
        nis: String,
        password: String
    ) {
        val result = repository.registerUser(name, nis, password)

        uiState = if (result.isSuccess) {
            uiState.copy(success = true, error = null)
        } else {
            uiState.copy(error = result.exceptionOrNull()?.message)
        }
    }

    fun login(
        nis: String,
        password: String,
        onSuccess: (String) -> Unit
    ) {
        val result = repository.loginUser(nis, password)

        if (result.isSuccess) {
            onSuccess(result.getOrThrow())
        } else {
            uiState = uiState.copy(
                error = result.exceptionOrNull()?.message
            )
        }
    }
}

data class AuthUiState(
    val success: Boolean = false,
    val error: String? = null
)