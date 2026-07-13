package com.diet.android.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class TokenRequest(
    val token: String
)

data class JwtAuthResponse(
    val accessToken: String
)

data class DietitianApplicationDto(
    val fullName: String,
    val email: String,
    val university: String,
    val diplomaNumber: String,
    val experienceYears: Int,
    val documentUrl: String?,
    val note: String?,
    val password: String
)

data class UserInfo(
    val id: Long,
    val email: String,
    val name: String?,
    val provider: String?,
    val providerId: String?,
    val role: String,
    val height: Double?,
    val currentWeight: Double?,
    val targetWeight: Double?,
    val category: String?,
    val notes: String?,
    val glp1InjectionDay: String?,
    val glp1Dosage: String?,
    val lipedemaStage: Int?,
    val antiInflammatoryCompliant: Boolean?,
    val hormoneTargetCycle: String?,
    val dietitianApplicationStatus: String?,
    val dietitianRejectionReason: String?,
    val instagramUrl: String?,
    val linkedinUrl: String?,
    val youtubeUrl: String?,
    val profilePictureUrl: String?,
    val fcmToken: String?
)

data class ProfileUpdateRequest(
    val height: Double?,
    val currentWeight: Double?,
    val targetWeight: Double?,
    val category: String?,
    val glp1InjectionDay: String? = null,
    val glp1Dosage: String? = null,
    val lipedemaStage: Int? = null,
    val antiInflammatoryCompliant: Boolean? = null,
    val hormoneTargetCycle: String? = null,
    val name: String? = null,
    val instagramUrl: String? = null,
    val linkedinUrl: String? = null,
    val youtubeUrl: String? = null,
    val notes: String? = null
)
