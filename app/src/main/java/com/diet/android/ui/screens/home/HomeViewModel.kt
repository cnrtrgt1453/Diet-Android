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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

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
    var todayDailyLog by mutableStateOf<DailyLog?>(null)

    // Dietitian states
    var dietitianStats by mutableStateOf<ClinicStats>(ClinicStats())
        private set
    var connectionRequests by mutableStateOf<List<DietitianConnectionRequest>>(emptyList())
        private set
    var dietitianAppointments by mutableStateOf<List<Appointment>>(emptyList())
        private set
    var dietitianSlots by mutableStateOf<List<DietitianAvailability>>(emptyList())
        private set

    // New states for migrated dialogs
    var notifications by mutableStateOf<List<AppNotification>>(emptyList())
        private set
    var unreadCount by mutableStateOf(0L)
        private set
    var dietitianApplications by mutableStateOf<List<DietitianApplication>>(emptyList())
        private set
    var availableSlots by mutableStateOf<List<DietitianAvailability>>(emptyList())
        private set
    var cohortsData by mutableStateOf<List<CohortDto>>(emptyList())
        private set
    var complianceData by mutableStateOf<List<CategoryComplianceDto>>(emptyList())
        private set
    var weightLossRates by mutableStateOf<List<ClientWeightLossRateDto>>(emptyList())
        private set

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun loadHomeData(context: Context) {
        isLoading = true
        viewModelScope.launch {
            try {
                val user = apiService.getCurrentUser()
                userInfo = user

                // Register Push Notifications FCM token safely
                val token = ApiClient.getSavedToken(context)
                if (!token.isNullOrEmpty()) {
                    com.diet.android.util.NotificationHelper.registerPushToken(context, token, apiService)
                }

                // Load notifications for all roles
                loadNotifications()

                if (user.role == "ROLE_DIETITIAN") {
                    dietitianStats = apiService.getDietitianStats()
                    connectionRequests = apiService.getPendingRequests()
                    dietitianAppointments = apiService.getDietitianAppointments(null)
                    loadDietitianSlots()
                    loadClinicAnalytics()
                    if (user.email == "suhedaterat2@gmail.com") {
                        loadAdminApplications()
                    }
                } else if (user.role == "ROLE_USER") {
                    try {
                        todayDiet = apiService.getTodayDiet()
                    } catch (e: Exception) {
                        todayDiet = null
                    }
                    try {
                        val logs = apiService.getMyDailyLogs(
                            startDate = java.time.LocalDate.now().toString(),
                            endDate = java.time.LocalDate.now().toString()
                        )
                        todayDailyLog = logs.firstOrNull()
                    } catch (e: Exception) {
                        todayDailyLog = null
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
                } else if (user.role == "ROLE_ADMIN") {
                    loadAdminApplications()
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
                val savedLog = apiService.saveDailyLog(log)
                todayDailyLog = savedLog
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

    // --- MIGRATED MODAL ACTIONS ---

    fun loadNotifications() {
        viewModelScope.launch {
            try {
                notifications = apiService.getMyNotifications()
                unreadCount = apiService.getUnreadCount()
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            try {
                apiService.markNotificationAsRead(id)
                loadNotifications()
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Bildirim okundu işaretlenemedi: ${e.localizedMessage}"))
            }
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            try {
                apiService.markAllNotificationsAsRead()
                loadNotifications()
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Tüm bildirimler okundu işaretlenemedi: ${e.localizedMessage}"))
            }
        }
    }

    fun loadAdminApplications() {
        viewModelScope.launch {
            try {
                dietitianApplications = apiService.getPendingApplications()
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Başvurular yüklenirken hata: ${e.localizedMessage}"))
            }
        }
    }

    fun startReviewApplication(id: Long) {
        viewModelScope.launch {
            try {
                apiService.startReviewApplication(id)
                loadAdminApplications()
                _uiEvent.emit(HomeUiEvent.ShowMessage("İnceleme başlatıldı."))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("İnceleme başlatılamadı: ${e.localizedMessage}"))
            }
        }
    }

    fun approveApplication(id: Long) {
        viewModelScope.launch {
            try {
                apiService.approveApplication(id)
                loadAdminApplications()
                _uiEvent.emit(HomeUiEvent.ShowMessage("Başvuru onaylandı."))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Başvuru onaylanamadı: ${e.localizedMessage}"))
            }
        }
    }

    fun rejectApplication(id: Long, reason: String) {
        viewModelScope.launch {
            try {
                apiService.rejectApplication(id, DietitianApplicationReviewDto(reason))
                loadAdminApplications()
                _uiEvent.emit(HomeUiEvent.ShowMessage("Başvuru reddedildi."))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Başvuru reddedilemedi: ${e.localizedMessage}"))
            }
        }
    }

    fun loadClinicAnalytics() {
        viewModelScope.launch {
            try {
                cohortsData = apiService.getCohortAnalysis()
                complianceData = apiService.getCategoryCompliance()
                weightLossRates = apiService.getClientWeightLossRates()
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    fun fetchAvailableSlots(dietitianId: Long, date: String) {
        viewModelScope.launch {
            try {
                availableSlots = apiService.getAvailableSlots(dietitianId, date)
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Müsait saatler yüklenemedi: ${e.localizedMessage}"))
            }
        }
    }

    fun addAvailabilitySlot(date: String, startTime: String, endTime: String) {
        viewModelScope.launch {
            try {
                apiService.createAvailabilitySlot(DietitianAvailability(date = date, startTime = startTime, endTime = endTime))
                _uiEvent.emit(HomeUiEvent.ShowMessage("Müsaitlik slotu başarıyla eklendi!"))
                loadDietitianSlots()
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Slot eklenemedi: ${e.localizedMessage}"))
            }
        }
    }

    fun loadDietitianSlots() {
        viewModelScope.launch {
            try {
                val slots = apiService.getMySlots()
                val today = java.time.LocalDate.now()
                val nowTime = java.time.LocalTime.now()
                
                val expiredSlots = mutableListOf<DietitianAvailability>()
                val activeSlots = mutableListOf<DietitianAvailability>()
                
                for (slot in slots) {
                    var isExpired = false
                    try {
                        val slotDate = java.time.LocalDate.parse(slot.date)
                        if (slotDate.isBefore(today)) {
                            isExpired = true
                        } else if (slotDate.isEqual(today)) {
                            val slotEndTime = java.time.LocalTime.parse(slot.endTime)
                            if (slotEndTime.isBefore(nowTime)) {
                                isExpired = true
                            }
                        }
                    } catch (e: Exception) {
                        // If parsing fails, keep it active to avoid accidental deletion
                    }
                    if (isExpired) {
                        expiredSlots.add(slot)
                    } else {
                        activeSlots.add(slot)
                    }
                }
                
                dietitianSlots = activeSlots
                
                // Asynchronously delete expired slots on backend
                expiredSlots.forEach { slot ->
                    slot.id?.let { id ->
                        launch {
                            try {
                                apiService.deleteAvailabilitySlot(id)
                            } catch (e: Exception) {
                                // Silent fail
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Mute error or print stack trace
            }
        }
    }

    fun deleteAvailabilitySlot(slotId: Long) {
        viewModelScope.launch {
            try {
                apiService.deleteAvailabilitySlot(slotId)
                _uiEvent.emit(HomeUiEvent.ShowMessage("Müsaitlik slotu silindi!"))
                loadDietitianSlots()
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Slot silinemedi: ${e.localizedMessage}"))
            }
        }
    }

    fun bookAppointmentSlot(slotId: Long, note: String?) {
        viewModelScope.launch {
            try {
                apiService.bookAppointmentSlot(slotId, BookingRequest(note))
                _uiEvent.emit(HomeUiEvent.ShowMessage("Randevu başarıyla rezerve edildi!"))
                userInfo?.let {
                    clientAppointments = apiService.getMyAppointments()
                }
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Randevu alınamadı: ${e.localizedMessage}"))
            }
        }
    }

    fun updateProfileDetails(
        name: String,
        notes: String?,
        instagramUrl: String?,
        linkedinUrl: String?,
        youtubeUrl: String?,
        xUrl: String?,
        facebookUrl: String?,
        profilePictureUrl: String?,
        height: Double?,
        currentWeight: Double?,
        targetWeight: Double?,
        category: String?,
        glp1InjectionDay: String?,
        glp1Dosage: String?
    ) {
        viewModelScope.launch {
            try {
                val updatedUser = apiService.updateProfile(
                    ProfileUpdateRequest(
                        name = name,
                        notes = notes,
                        instagramUrl = instagramUrl,
                        linkedinUrl = linkedinUrl,
                        youtubeUrl = youtubeUrl,
                        xUrl = xUrl,
                        facebookUrl = facebookUrl,
                        profilePictureUrl = profilePictureUrl,
                        height = height,
                        currentWeight = currentWeight,
                        targetWeight = targetWeight,
                        category = category,
                        glp1InjectionDay = glp1InjectionDay,
                        glp1Dosage = glp1Dosage
                    )
                )
                userInfo = updatedUser
                _uiEvent.emit(HomeUiEvent.ShowMessage("Profil başarıyla güncellendi!"))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Profil güncellenemedi: ${e.localizedMessage}"))
            }
        }
    }

    fun uploadProfilePicture(context: android.content.Context, uri: android.net.Uri) {
        isLoading = true
        viewModelScope.launch {
            try {
                val file = java.io.File(context.cacheDir, "temp_profile_picture.jpg")
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                val mediaType = (context.contentResolver.getType(uri) ?: "image/*").toMediaTypeOrNull()
                val requestFile = file.asRequestBody(mediaType)
                val body = okhttp3.MultipartBody.Part.createFormData("file", file.name, requestFile)

                val updatedUser = apiService.uploadProfilePicture(body)
                userInfo = updatedUser
                _uiEvent.emit(HomeUiEvent.ShowMessage("Profil fotoğrafı başarıyla güncellendi!"))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.Error("Profil fotoğrafı yüklenemedi: ${e.localizedMessage}"))
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
