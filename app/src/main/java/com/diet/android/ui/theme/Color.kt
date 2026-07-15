package com.diet.android.ui.theme

import androidx.compose.ui.graphics.Color

// Premium Design Palette
val DeepSlate = Color(0xFF0F172A)          // Dark background / Primary Dark color
val SlateGray = Color(0xFF334155)          // Secondary Dark / Text Secondary Dark
val EmeraldGreen = Color(0xFF10B981)       // Primary Green (Gains, Success)
val EmeraldLight = Color(0xFFD1FAE5)       // Soft success background
val RoseRed = Color(0xFFEF4444)            // Error / Losses (Losses, Alert)
val RoseLight = Color(0xFFFEE2E2)          // Soft error background

// Light Mode Colors (Material 3 roles)
val MD3LightPrimary = EmeraldGreen
val MD3LightOnPrimary = Color.White
val MD3LightPrimaryContainer = EmeraldLight
val MD3LightOnPrimaryContainer = Color(0xFF064E3B)
val MD3LightSecondary = SlateGray
val MD3LightOnSecondary = Color.White
val MD3LightBackground = Color(0xFFF8FAFC) // Slate-tinted very light gray (clean, modern background)
val MD3LightOnBackground = DeepSlate
val MD3LightSurface = Color.White
val MD3LightOnSurface = DeepSlate
val MD3LightSurfaceVariant = Color(0xFFF1F5F9) // Slate 100 for elegant elements
val MD3LightOnSurfaceVariant = SlateGray
val MD3LightOutline = Color(0xFFE2E8F0) // Slate 200 for subtle borders
val MD3LightError = RoseRed
val MD3LightOnError = Color.White

// Dark Mode Colors
val MD3DarkPrimary = Color(0xFF34D399) // Brighter emerald green for dark mode
val MD3DarkOnPrimary = Color(0xFF022C22)
val MD3DarkPrimaryContainer = Color(0xFF064E3B)
val MD3DarkOnPrimaryContainer = Color(0xFFD1FAE5)
val MD3DarkSecondary = Color(0xFF94A3B8)
val MD3DarkOnSecondary = Color(0xFF1E293B)
val MD3DarkBackground = DeepSlate
val MD3DarkOnBackground = Color(0xFFF8FAFC)
val MD3DarkSurface = Color(0xFF1E293B) // Dark surface card
val MD3DarkOnSurface = Color(0xFFF8FAFC)
val MD3DarkSurfaceVariant = Color(0xFF334155)
val MD3DarkOnSurfaceVariant = Color(0xFFCBD5E1)
val MD3DarkOutline = Color(0xFF475569)
val MD3DarkError = Color(0xFFF87171)
val MD3DarkOnError = Color(0xFF7F1D1D)

// Backward Compatibility Variables
val GreenPrimary = EmeraldGreen
val GreenSecondary = Color(0xFF34D399)
val GreenTertiary = Color(0xFF064E3B)
val BackgroundLight = Color(0xFFF8FAFC)
val BackgroundElementLight = Color.White
val TextDark = DeepSlate
val TextSecondaryDark = SlateGray
