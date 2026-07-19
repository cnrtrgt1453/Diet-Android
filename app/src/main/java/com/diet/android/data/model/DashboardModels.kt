package com.diet.android.data.model

import com.google.gson.annotations.SerializedName

data class DietPlan(
    val id: Long,
    val title: String?,
    val date: String,
    val breakfast: String?,
    val lunch: String?,
    val dinner: String?,
    val snacks: String?,
    val targetCalories: Int?,
    val targetProteinGrams: Int?,
    val targetCarbsGrams: Int?,
    val targetFatGrams: Int?,
    val completed: Boolean
)

data class Appointment(
    val id: Long,
    val client: UserInfo,
    val dietitian: UserInfo,
    val appointmentDate: String,
    val appointmentTime: String,
    val note: String?,
    val status: String
)

data class DietitianConnectionRequest(
    val id: Long,
    val client: UserInfo,
    val dietitian: UserInfo,
    val status: String
)

data class ClinicStats(
    val total: Int = 0,
    val glp1: Int = 0,
    val lipedema: Int = 0,
    val hormonalBalance: Int = 0,
    val weightManagement: Int = 0
)

data class CorrelationData(
    val waterIntakeCorrelation: Double?,
    val physicalActivityCorrelation: Double?,
    val glutenFreeCorrelation: Double?,
    val sugarFreeCorrelation: Double?,
    val dairyFreeCorrelation: Double?,
    val processedFoodFreeCorrelation: Double?,
    val alcoholFreeCorrelation: Double?
)

data class PredictionData(
    val targetAchievedDate: String?,
    val daysRemaining: Int?
)

data class DailyLog(
    val id: Long? = null,
    val date: String,
    val waterIntakeMl: Int = 0,
    val physicalActivityMinutes: Int = 0,
    val calorieIntake: Int = 0,
    val note: String? = null,
    val sideEffectLevel: Int? = null,
    val glp1Nausea: Int? = null,
    val glp1Constipation: Int? = null,
    val glp1Diarrhea: Int? = null,
    val glp1Vomiting: Boolean? = null,
    val glp1InjectionSite: String? = null,
    val sideEffectsList: String? = null,
    val painLevel: Int? = null,
    val lipedemaPainLevelVas: Int? = null,
    val glutenFree: Boolean? = null,
    val sugarFree: Boolean? = null,
    val dairyFree: Boolean? = null,
    val processedFoodFree: Boolean? = null,
    val alcoholFree: Boolean? = null,
    val hormonalPhase: String? = null,
    val fastingBloodGlucose: Double? = null,
    val insulinLevel: Double? = null,
    val cycleDay: Int? = null,
    val insulinCraving: Int? = null
)

data class Measurement(
    val id: Long? = null,
    val date: String,
    val weight: Double,
    val bodyFat: Double? = null,
    val muscleMass: Double? = null,
    val ankleCircumference: Double? = null,
    val calfCircumference: Double? = null,
    val thighCircumference: Double? = null,
    val note: String? = null
)

data class DietPlanTemplate(
    val id: Long,
    val title: String,
    val breakfast: String?,
    val lunch: String?,
    val dinner: String?,
    val snacks: String?,
    val targetCalories: Int?,
    val targetProteinGrams: Int?,
    val targetCarbsGrams: Int?,
    val targetFatGrams: Int?
)

data class ChatMessage(
    val id: Long,
    val sender: UserInfo,
    val recipient: UserInfo?,
    val content: String,
    val isBroadcast: Boolean,
    val isRead: Boolean,
    val sentAt: String?
)

data class MessageRequest(
    val content: String
)

data class ConversationSummary(
    val partnerId: Long,
    val partnerName: String?,
    val partnerEmail: String?,
    val partnerCategory: String?,
    val lastMessage: String?,
    val lastMessageSentAt: String?,
    val unreadCount: Long
)

data class DietitianAvailability(
    val id: Long? = null,
    val date: String,
    val startTime: String,
    val endTime: String,
    val isBooked: Boolean = false
)

data class BookingRequest(
    val note: String?
)

data class AppNotification(
    val id: Long,
    val title: String,
    val message: String,
    @SerializedName("read") val isRead: Boolean,
    val createdAt: String
)

data class DietitianApplication(
    val id: Long,
    val user: UserInfo,
    val fullName: String,
    val email: String,
    val university: String?,
    val diplomaNumber: String?,
    val experienceYears: Int?,
    val documentUrl: String?,
    val note: String?,
    val status: String, // PENDING, UNDER_REVIEW, APPROVED, REJECTED
    val rejectionReason: String?,
    val createdAt: String?
)

data class DietitianApplicationReviewDto(
    val rejectionReason: String
)

data class CohortDto(
    val cohortMonth: String,
    val totalClients: Long,
    val averageStartingWeight: Double,
    val averageCurrentWeight: Double,
    val averageWeightLoss: Double
)

data class CategoryComplianceDto(
    val category: String,
    val complianceRate: Double
)

data class ClientWeightLossRateDto(
    val clientName: String,
    val category: String,
    val startingWeight: Double,
    val currentWeight: Double,
    val weightLossRateKgPerWeek: Double,
    val daysTracked: Long
)

