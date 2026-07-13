package com.diet.android.ui.screens.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.diet.android.data.api.ApiClient
import com.diet.android.data.api.ApiService
import com.diet.android.data.model.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface HomeUiEvent {
    data class Error(val message: String) : HomeUiEvent
    data class ShowMessage(val message: String) : HomeUiEvent
}

class HomeViewModel(private val apiService: ApiService) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var userInfo by mutableStateOf<UserInfo?>(null)
        private set

    // Client states
    var todayDiet by mutableStateOf<DietPlan?>(null)
        private set
    var correlationData by mutableStateOf<CorrelationData?>(null)
        private set
    var predictionData by mutableStateOf<PredictionData?>(null)
        private set
    var clientAppointments by mutableStateOf<List<Appointment>>(emptyList())
        private set

    // Dietitian states
    var dietitianStats by mutableStateOf<ClinicStats>(ClinicStats())
        private set
    var connectionRequests by mutableStateOf<List<DietitianConnectionRequest>>(emptyList())
        private set
    var dietitianAppointments by mutableStateOf<List<Appointment>>(emptyList())
        private set

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun loadHomeData(context: Context) {
        isLoading = true
        viewModelScope.launch {
            try {
                val user = apiService.getCurrentUser()
                userInfo = user

                if (user.role == "ROLE_DIETITIAN") {
                    dietitianStats = apiService.getDietitianStats()
                    connectionRequests = apiService.getPendingRequests()
                    dietitianAppointments = apiService.getDietitianAppointments(null)
                } else if (user.role == "ROLE_USER") {
                    try {
                        todayDiet = apiService.getTodayDiet()
                    } catch (e: Exception) {
                        todayDiet = null
                    }
                    try {
                        correlationData = apiService.getCorrelationAnalysis(user.id)
                    } catch (e: Exception) {
                        correlationData = null
                    }
                    try {
                        predictionData = apiService.getWeightPrediction(user.id)
                    } catch (e: Exception) {
                        predictionData = null
                    }
                    clientAppointments = apiService.getMyAppointments()
                }
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Veriler yüklenirken hata oluştu: ${e.localizedMessage}"))
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleDietCompleted(dietId: Long) {
        viewModelScope.launch {
            try {
                val updated = apiService.toggleDietCompleted(dietId)
                todayDiet = updated
                _uiEvent.emit(HomeUiEvent.ShowMessage("Diyet planı başarıyla güncellendi!"))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Güncelleme hatası: ${e.localizedMessage}"))
            }
        }
    }

    fun saveDailyLog(log: DailyLog) {
        isLoading = true
        viewModelScope.launch {
            try {
                apiService.saveDailyLog(log)
                _uiEvent.emit(HomeUiEvent.ShowMessage("Günlük takip kaydınız başarıyla kaydedildi!"))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Kayıt hatası: ${e.localizedMessage}"))
            } finally {
                isLoading = false
            }
        }
    }

    fun approveRequest(requestId: Long) {
        isLoading = true
        viewModelScope.launch {
            try {
                apiService.approveConnectionRequest(requestId)
                connectionRequests = connectionRequests.filter { it.id != requestId }
                dietitianStats = apiService.getDietitianStats()
                _uiEvent.emit(HomeUiEvent.ShowMessage("Çalışma talebi kabul edildi!"))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Kabul etme hatası: ${e.localizedMessage}"))
            } finally {
                isLoading = false
            }
        }
    }

    fun rejectRequest(requestId: Long) {
        isLoading = true
        viewModelScope.launch {
            try {
                apiService.rejectConnectionRequest(requestId)
                connectionRequests = connectionRequests.filter { it.id != requestId }
                _uiEvent.emit(HomeUiEvent.ShowMessage("Çalışma talebi reddedildi!"))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Reddetme hatası: ${e.localizedMessage}"))
            } finally {
                isLoading = false
            }
        }
    }

    fun updateAppointmentStatus(appointmentId: Long, status: String) {
        isLoading = true
        viewModelScope.launch {
            try {
                apiService.updateAppointmentStatus(appointmentId, status)
                dietitianAppointments = apiService.getDietitianAppointments(null)
                _uiEvent.emit(HomeUiEvent.ShowMessage("Randevu durumu güncellendi!"))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Güncelleme hatası: ${e.localizedMessage}"))
            } finally {
                isLoading = false
            }
        }
    }
}

class HomeViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
