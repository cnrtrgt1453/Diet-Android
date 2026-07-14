package com.diet.android.util

import android.content.Context
import android.util.Log
import com.diet.android.data.api.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

object NotificationHelper {
    private const val TAG = "NotificationHelper"
    private const val PREFS_NAME = "diet_notifications_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"

    fun registerPushToken(context: Context, userToken: String, apiService: ApiService) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                var token = sharedPreferences.getString(KEY_FCM_TOKEN, null)
                if (token.isNullOrEmpty()) {
                    token = "mock-fcm-" + UUID.randomUUID().toString()
                    sharedPreferences.edit().putString(KEY_FCM_TOKEN, token).apply()
                }

                Log.d(TAG, "Registering FCM token: $token")
                apiService.updateFcmToken(mapOf("fcmToken" to token))
                Log.d(TAG, "FCM token registered successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register FCM token", e)
            }
        }
    }
}
