package com.diet.android.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.diet.android.data.model.ProfileUpdateRequest
import com.diet.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface ProfileUiEvent {
    object Success : ProfileUiEvent
    data class Error(val message: String) : ProfileUiEvent
}

class CompleteProfileViewModel(private val authRepository: AuthRepository) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun submitProfile(
        height: String,
        currentWeight: String,
        targetWeight: String,
        category: String,
        glp1InjectionDay: String?,
        glp1Dosage: String?,
        lipedemaStage: Int?,
        antiInflammatoryCompliant: Boolean?,
        hormoneTargetCycle: String?
    ) {
        val heightVal = height.toDoubleOrNull()
        val currentWeightVal = currentWeight.toDoubleOrNull()
        val targetWeightVal = targetWeight.toDoubleOrNull()

        if (heightVal == null || currentWeightVal == null || targetWeightVal == null) {
            viewModelScope.launch { _uiEvent.emit(ProfileUiEvent.Error("Lütfen boy, mevcut ağırlık ve hedef ağırlık değerlerini geçerli sayılar olarak giriniz.")) }
            return
        }

        isLoading = true
        viewModelScope.launch {
            val request = ProfileUpdateRequest(
                height = heightVal,
                currentWeight = currentWeightVal,
                targetWeight = targetWeightVal,
                category = category,
                glp1InjectionDay = if (category == "GLP_1") glp1InjectionDay else null,
                glp1Dosage = if (category == "GLP_1") glp1Dosage else null,
                lipedemaStage = if (category == "LIPEDEMA") lipedemaStage else null,
                antiInflammatoryCompliant = if (category == "LIPEDEMA") antiInflammatoryCompliant else null,
                hormoneTargetCycle = if (category == "HORMONAL_BALANCE") hormoneTargetCycle else null
            )

            authRepository.updateProfile(request)
                .onSuccess {
                    _uiEvent.emit(ProfileUiEvent.Success)
                }
                .onFailure { error ->
                    _uiEvent.emit(ProfileUiEvent.Error(error.localizedMessage ?: "Profil kaydedilirken bir hata oluştu."))
                }
            isLoading = false
        }
    }
}

class CompleteProfileViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompleteProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CompleteProfileViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
