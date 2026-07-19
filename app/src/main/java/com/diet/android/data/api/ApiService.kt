package com.diet.android.data.api

import com.diet.android.data.model.*
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/v1/auth/login")
    suspend fun loginWithPassword(@Body request: LoginRequest): JwtAuthResponse

    @POST("api/v1/auth/google")
    suspend fun loginWithGoogle(@Body request: TokenRequest): JwtAuthResponse

    @POST("api/v1/auth/facebook")
    suspend fun loginWithFacebook(@Body request: TokenRequest): JwtAuthResponse

    @POST("api/v1/auth/forgot-password")
    suspend fun forgotPassword(@Query("email") email: String): ResponseBody

    @POST("api/v1/auth/apply-dietitian")
    suspend fun applyDietitian(@Body request: DietitianApplicationDto): ResponseBody

    @PUT("api/v1/users/profile")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): UserInfo

    @retrofit2.http.Multipart
    @POST("api/v1/users/profile-picture")
    suspend fun uploadProfilePicture(
        @retrofit2.http.Part file: okhttp3.MultipartBody.Part
    ): UserInfo

    @GET("api/v1/test/me")
    suspend fun getCurrentUser(): UserInfo

    // Client Dashboard
    @GET("api/v1/diets/my/today")
    suspend fun getTodayDiet(): DietPlan

    @POST("api/v1/diets/my/{dietId}/toggle")
    suspend fun toggleDietCompleted(@Path("dietId") dietId: Long): DietPlan

    @POST("api/v1/logs/daily")
    suspend fun saveDailyLog(@Body log: DailyLog): DailyLog

    @GET("api/v1/logs/daily/my")
    suspend fun getMyDailyLogs(
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?
    ): List<DailyLog>

    @GET("api/v1/appointments/my")
    suspend fun getMyAppointments(): List<Appointment>

    @GET("api/v1/analytics/client/{clientId}/correlation")
    suspend fun getCorrelationAnalysis(@Path("clientId") clientId: Long): CorrelationData

    @GET("api/v1/analytics/client/{clientId}/prediction")
    suspend fun getWeightPrediction(@Path("clientId") clientId: Long): PredictionData

    // Dietitian Dashboard
    @GET("api/v1/clients/stats")
    suspend fun getDietitianStats(): ClinicStats

    @GET("api/v1/connections/pending-requests")
    suspend fun getPendingRequests(): List<DietitianConnectionRequest>

    @POST("api/v1/connections/requests/{requestId}/approve")
    suspend fun approveConnectionRequest(@Path("requestId") requestId: Long): ResponseBody

    @POST("api/v1/connections/requests/{requestId}/reject")
    suspend fun rejectConnectionRequest(@Path("requestId") requestId: Long): ResponseBody

    // Explore & Chat (Phase 5)
    @GET("api/v1/clients")
    suspend fun getClients(): List<UserInfo>

    @GET("api/v1/clients/{clientId}/measurements")
    suspend fun getClientMeasurements(@Path("clientId") clientId: Long): List<Measurement>

    @POST("api/v1/clients/{clientId}/measurements")
    suspend fun addClientMeasurement(
        @Path("clientId") clientId: Long,
        @Body measurement: Measurement
    ): Measurement

    @GET("api/v1/clients/{clientId}/diets")
    suspend fun getClientDiets(@Path("clientId") clientId: Long): List<DietPlan>

    @POST("api/v1/clients/{clientId}/diets")
    suspend fun addClientDiet(
        @Path("clientId") clientId: Long,
        @Body diet: DietPlan
    ): DietPlan

    @GET("api/v1/logs/daily/client/{clientId}")
    suspend fun getClientDailyLogs(@Path("clientId") clientId: Long): List<DailyLog>

    @GET("api/v1/diet-templates")
    suspend fun getDietTemplates(): List<DietPlanTemplate>

    @POST("api/v1/diet-templates/from-plan/{planId}")
    suspend fun saveTemplateFromPlan(
        @Path("planId") planId: Long,
        @Query("title") title: String
    ): ResponseBody

    @GET("api/v1/messages/history/{otherUserId}")
    suspend fun getChatHistory(@Path("otherUserId") otherUserId: Long): List<ChatMessage>

    @GET("api/v1/messages/inbox")
    suspend fun getInbox(): List<ConversationSummary>

    @POST("api/v1/messages/send/{recipientId}")
    suspend fun sendChatMessage(
        @Path("recipientId") recipientId: Long,
        @Body request: MessageRequest
    ): ChatMessage

    // Dietitian Appointments & Takvim
    @GET("api/v1/appointments/dietitian")
    suspend fun getDietitianAppointments(@Query("status") status: String?): List<Appointment>

    @POST("api/v1/appointments/{id}/status")
    suspend fun updateAppointmentStatus(
        @Path("id") id: Long,
        @Query("status") status: String
    ): ResponseBody

    @GET("api/v1/appointments/availability/dietitian/{dietitianId}")
    suspend fun getAvailableSlots(
        @Path("dietitianId") dietitianId: Long,
        @Query("date") date: String
    ): List<DietitianAvailability>

    @POST("api/v1/appointments/availability")
    suspend fun createAvailabilitySlot(@Body slot: DietitianAvailability): DietitianAvailability

    @GET("api/v1/appointments/availability/my-slots")
    suspend fun getMySlots(): List<DietitianAvailability>

    @DELETE("api/v1/appointments/availability/{slotId}")
    suspend fun deleteAvailabilitySlot(@Path("slotId") slotId: Long): okhttp3.ResponseBody

    @POST("api/v1/appointments/book-slot/{slotId}")
    suspend fun bookAppointmentSlot(
        @Path("slotId") slotId: Long,
        @Body request: BookingRequest
    ): Appointment

    // Notifications
    @GET("api/v1/notifications")
    suspend fun getMyNotifications(): List<AppNotification>

    @GET("api/v1/notifications/unread/count")
    suspend fun getUnreadCount(): Long

    @POST("api/v1/notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: Long): ResponseBody

    @POST("api/v1/notifications/read-all")
    suspend fun markAllNotificationsAsRead(): ResponseBody

    // Admin Applications Review
    @GET("api/v1/admin/applications")
    suspend fun getPendingApplications(): List<DietitianApplication>

    @POST("api/v1/admin/applications/{id}/start-review")
    suspend fun startReviewApplication(@Path("id") id: Long): DietitianApplication

    @POST("api/v1/admin/applications/{id}/approve")
    suspend fun approveApplication(@Path("id") id: Long): DietitianApplication

    @POST("api/v1/admin/applications/{id}/reject")
    suspend fun rejectApplication(
        @Path("id") id: Long,
        @Body request: DietitianApplicationReviewDto
    ): DietitianApplication

    // Dietitian Analytics
    @GET("api/v1/analytics/dietitian/cohorts")
    suspend fun getCohortAnalysis(): List<CohortDto>

    @GET("api/v1/analytics/dietitian/compliance")
    suspend fun getCategoryCompliance(): List<CategoryComplianceDto>

    @GET("api/v1/analytics/dietitian/rates")
    suspend fun getClientWeightLossRates(): List<ClientWeightLossRateDto>

    // FCM token
    @POST("api/v1/users/fcm-token")
    suspend fun updateFcmToken(@Body request: Map<String, String>): ResponseBody
}
