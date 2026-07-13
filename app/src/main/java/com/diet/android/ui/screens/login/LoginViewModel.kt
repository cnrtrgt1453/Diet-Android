package com.diet.android.ui.screens.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.diet.android.data.api.ApiClient
import com.diet.android.data.model.DietitianApplicationDto
import com.diet.android.data.model.LoginRequest
import com.diet.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface LoginUiEvent {
    data class Success(val role: String, val isProfileComplete: Boolean, val applicationStatus: String?) : LoginUiEvent
    data class Error(val message: String) : LoginUiEvent
    data class ShowAlert(val title: String, val message: String, val isSuccess: Boolean) : LoginUiEvent
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun loginWithPassword(context: Context, email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch { _uiEvent.emit(LoginUiEvent.Error("Lütfen tüm alanları doldurunuz.")) }
            return
        }
        isLoading = true
        viewModelScope.launch {
            authRepository.loginWithPassword(LoginRequest(email, password))
                .onSuccess { jwtResponse ->
                    ApiClient.saveToken(context, jwtResponse.accessToken)
                    authRepository.getCurrentUser()
                        .onSuccess { userInfo ->
                            val isComplete = userInfo.height != null && userInfo.category != null
                            _uiEvent.emit(
                                LoginUiEvent.Success(
                                    role = userInfo.role,
                                    isProfileComplete = isComplete,
                                    applicationStatus = userInfo.dietitianApplicationStatus
                                )
                            )
                        }
                        .onFailure {
                            ApiClient.clearToken(context)
                            _uiEvent.emit(LoginUiEvent.Error("Kullanıcı bilgileri alınamadı."))
                        }
                }
                .onFailure { error ->
                    _uiEvent.emit(LoginUiEvent.Error(error.localizedMessage ?: "E-posta veya şifre hatalı."))
                }
            isLoading = false
        }
    }

    fun loginWithSocial(context: Context, provider: String, token: String) {
        isLoading = true
        viewModelScope.launch {
            val result = if (provider == "google") {
                authRepository.loginWithGoogle(token)
            } else {
                authRepository.loginWithFacebook(token)
            }
            
            result.onSuccess { jwtResponse ->
                ApiClient.saveToken(context, jwtResponse.accessToken)
                authRepository.getCurrentUser()
                    .onSuccess { userInfo ->
                        val isComplete = userInfo.height != null && userInfo.category != null
                        _uiEvent.emit(
                            LoginUiEvent.Success(
                                role = userInfo.role,
                                isProfileComplete = isComplete,
                                applicationStatus = userInfo.dietitianApplicationStatus
                            )
                        )
                    }
                    .onFailure {
                        ApiClient.clearToken(context)
                        _uiEvent.emit(LoginUiEvent.Error("Sosyal kullanıcı bilgileri alınamadı."))
                    }
            }.onFailure { error ->
                _uiEvent.emit(LoginUiEvent.Error(error.localizedMessage ?: "Kimlik doğrulama başarısız."))
            }
            isLoading = false
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            viewModelScope.launch { _uiEvent.emit(LoginUiEvent.Error("Lütfen e-posta alanını doldurunuz.")) }
            return
        }
        isLoading = true
        viewModelScope.launch {
            authRepository.forgotPassword(email.trim())
                .onSuccess { msg ->
                    _uiEvent.emit(LoginUiEvent.ShowAlert("Başarılı", msg, true))
                }
                .onFailure { error ->
                    _uiEvent.emit(LoginUiEvent.Error(error.localizedMessage ?: "Şifre sıfırlama başarısız."))
                }
            isLoading = false
        }
    }

    fun applyDietitian(dto: DietitianApplicationDto) {
        isLoading = true
        viewModelScope.launch {
            authRepository.applyDietitian(dto)
                .onSuccess {
                    _uiEvent.emit(
                        LoginUiEvent.ShowAlert(
                            "Başarılı",
                            "Diyetisyen başvurunuz alındı. Giriş ekranından başvuru e-postanız ve şifrenizle giriş yaparak başvuru durumunuzu takip edebilirsiniz.",
                            true
                        )
                    )
                }
                .onFailure { error ->
                    _uiEvent.emit(LoginUiEvent.Error(error.localizedMessage ?: "Başvuru sırasında hata oluştu."))
                }
            isLoading = false
        }
    }
}

class LoginViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
