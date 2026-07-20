package com.diet.android.ui.screens.explore

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
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

sealed interface ExploreUiEvent {
    data class Error(val message: String) : ExploreUiEvent
    data class ShowMessage(val message: String) : ExploreUiEvent
    object DismissModals : ExploreUiEvent
}

class ExploreViewModel(private val apiService: ApiService) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var userInfo by mutableStateOf<UserInfo?>(null)
        private set

    // Dietitian specific
    var clients by mutableStateOf<List<UserInfo>>(emptyList())
        private set
    var filteredClients by mutableStateOf<List<UserInfo>>(emptyList())
        private set
    var templates by mutableStateOf<List<DietPlanTemplate>>(emptyList())
        private set

    // Selected client detail
    var selectedClient by mutableStateOf<UserInfo?>(null)
        private set
    var clientMeasurements by mutableStateOf<List<Measurement>>(emptyList())
        private set
    var clientDiets by mutableStateOf<List<DietPlan>>(emptyList())
        private set
    var clientDailyLogs by mutableStateOf<List<DailyLog>>(emptyList())
        private set
    var clientPrediction by mutableStateOf<PredictionData?>(null)
        private set
    var clientCorrelation by mutableStateOf<CorrelationData?>(null)
        private set

    // Client specific
    var myMeasurements by mutableStateOf<List<Measurement>>(emptyList())
        private set
    var myDiets by mutableStateOf<List<DietPlan>>(emptyList())
        private set
    var myDailyLogs by mutableStateOf<List<DailyLog>>(emptyList())
        private set

    // Chat specific
    var chatWithUser by mutableStateOf<UserInfo?>(null)
        private set
    var chatMessages by mutableStateOf<List<ChatMessage>>(emptyList())
        private set
    var isWebSocketConnected by mutableStateOf(false)
        private set
    var pastMonthChatMessages by mutableStateOf<List<ChatMessage>>(emptyList())
        private set
    var inboxList by mutableStateOf<List<ConversationSummary>>(emptyList())
        private set

    fun loadInbox() {
        isLoading = true
        viewModelScope.launch {
            try {
                inboxList = apiService.getInbox()
            } catch (e: Exception) {
                _uiEvent.emit(ExploreUiEvent.Error("Gelen kutusu yüklenemedi: ${e.localizedMessage}"))
            } finally {
                isLoading = false
            }
        }
    }

    private val _uiEvent = MutableSharedFlow<ExploreUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun loadInitialData(context: Context) {
        isLoading = true
        viewModelScope.launch {
            try {
                val me = apiService.getCurrentUser()
                userInfo = me

                if (me.role == "ROLE_DIETITIAN") {
                    val loadedClients = apiService.getClients()
                    clients = loadedClients
                    filteredClients = loadedClients
                } else {
                    try {
                        myMeasurements = apiService.getClientMeasurements(me.id)
                    } catch (e: Exception) {
                        myMeasurements = emptyList()
                    }
                    try {
                        myDiets = apiService.getClientDiets(me.id)
                    } catch (e: Exception) {
                        myDiets = emptyList()
                    }
                    try {
                        myDailyLogs = apiService.getMyDailyLogs(null, null)
                    } catch (e: Exception) {
                        myDailyLogs = emptyList()
                    }
                    if (me.dietitian != null) {
                        try {
                            val history = apiService.getChatHistory(me.dietitian.id)
                            val oneMonthAgo = java.time.LocalDateTime.now().minusDays(30)
                            pastMonthChatMessages = history.filter { msg ->
                                try {
                                    val dt = java.time.LocalDateTime.parse(msg.sentAt)
                                    dt.isAfter(oneMonthAgo)
                                } catch (e: Exception) {
                                    true
                                }
                            }
                        } catch (e: Exception) {
                            pastMonthChatMessages = emptyList()
                        }
                    } else {
                        pastMonthChatMessages = emptyList()
                    }
                }
            } catch (e: Exception) {
                _uiEvent.emit(ExploreUiEvent.Error("Veriler yüklenemedi: ${e.localizedMessage}"))
            } finally {
                isLoading = false
            }
        }
    }

    fun reloadInitialData() {
        viewModelScope.launch {
            try {
                val me = apiService.getCurrentUser()
                userInfo = me

                if (me.role == "ROLE_DIETITIAN") {
                    val loadedClients = apiService.getClients()
                    clients = loadedClients
                    filteredClients = loadedClients
                } else {
                    try {
                        myMeasurements = apiService.getClientMeasurements(me.id)
                    } catch (e: Exception) {
                        myMeasurements = emptyList()
                    }
                    try {
                        myDiets = apiService.getClientDiets(me.id)
                    } catch (e: Exception) {
                        myDiets = emptyList()
                    }
                    try {
                        myDailyLogs = apiService.getMyDailyLogs(null, null)
                    } catch (e: Exception) {
                        myDailyLogs = emptyList()
                    }
                    if (me.dietitian != null) {
                        try {
                            val history = apiService.getChatHistory(me.dietitian.id)
                            val oneMonthAgo = java.time.LocalDateTime.now().minusDays(30)
                            pastMonthChatMessages = history.filter { msg ->
                                try {
                                    val dt = java.time.LocalDateTime.parse(msg.sentAt)
                                    dt.isAfter(oneMonthAgo)
                                } catch (e: Exception) {
                                    true
                                }
                            }
                        } catch (e: Exception) {
                            pastMonthChatMessages = emptyList()
                        }
                    } else {
                        pastMonthChatMessages = emptyList()
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun filterClients(query: String, category: String) {
        var filtered = clients
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.name?.contains(query, ignoreCase = true) == true ||
                it.email?.contains(query, ignoreCase = true) == true
            }
        }
        if (category != "ALL") {
            filtered = filtered.filter { it.category == category }
        }
        filteredClients = filtered
    }

    fun selectClient(client: UserInfo) {
        selectedClient = client
        isLoading = true
        viewModelScope.launch {
            try {
                clientMeasurements = apiService.getClientMeasurements(client.id)
                clientDiets = apiService.getClientDiets(client.id)
                try {
                    clientDailyLogs = apiService.getClientDailyLogs(client.id)
                } catch (e: Exception) {
                    clientDailyLogs = emptyList()
                }
                try {
                    clientPrediction = apiService.getWeightPrediction(client.id)
                } catch (e: Exception) {
                    clientPrediction = null
                }
                try {
                    clientCorrelation = apiService.getCorrelationAnalysis(client.id)
                } catch (e: Exception) {
                    clientCorrelation = null
                }
            } catch (e: Exception) {
                _uiEvent.emit(ExploreUiEvent.Error("Danışan detayları yüklenemedi: ${e.localizedMessage}"))
            } finally {
                isLoading = false
            }
        }
    }

    fun loadTemplates() {
        viewModelScope.launch {
            try {
                templates = apiService.getDietTemplates()
            } catch (e: Exception) {
                templates = emptyList()
            }
        }
    }

    fun addMeasurement(measurement: Measurement) {
        val client = selectedClient ?: return
        isLoading = true
        viewModelScope.launch {
            try {
                val added = apiService.addClientMeasurement(client.id, measurement)
                clientMeasurements = listOf(added) + clientMeasurements
                _uiEvent.emit(ExploreUiEvent.ShowMessage("Ölçüm başarıyla eklendi!"))
            } catch (e: Exception) {
                _uiEvent.emit(ExploreUiEvent.Error("Ölçüm eklenemedi: ${e.localizedMessage}"))
            } finally {
                isLoading = false
            }
        }
    }

    fun addDiet(diet: DietPlan, saveAsTemplate: Boolean, templateTitle: String) {
        val client = selectedClient ?: return
        isLoading = true
        viewModelScope.launch {
            try {
                val added = apiService.addClientDiet(client.id, diet)
                clientDiets = listOf(added) + clientDiets

                if (saveAsTemplate && templateTitle.isNotBlank()) {
                    try {
                        apiService.saveTemplateFromPlan(added.id, templateTitle)
                    } catch (e: Exception) {
                        // ignore
                    }
                }
                _uiEvent.emit(ExploreUiEvent.ShowMessage("Diyet başarıyla atandı!"))
            } catch (e: Exception) {
                _uiEvent.emit(ExploreUiEvent.Error("Diyet atanamadı: ${e.localizedMessage}"))
            } finally {
                isLoading = false
            }
        }
    }

    fun startChat(partner: UserInfo, context: Context) {
        chatWithUser = partner
        chatMessages = emptyList()
        isWebSocketConnected = false
        val token = ApiClient.getSavedToken(context) ?: return

        viewModelScope.launch {
            try {
                chatMessages = apiService.getChatHistory(partner.id)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit(ExploreUiEvent.Error("Sohbet geçmişi yüklenemedi: ${e.localizedMessage}"))
            }
        }

        val wsUrl = "ws://10.0.2.2:8080/ws/chat?token=$token"
        val request = Request.Builder().url(wsUrl).build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                super.onOpen(webSocket, response)
                isWebSocketConnected = true
                android.util.Log.i("ExploreViewModel", "WebSocket connection opened successfully.")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                viewModelScope.launch(Dispatchers.Main) {
                    try {
                        val json = JSONObject(text)
                        if (json.optString("type") == "message") {
                            val dataObj = json.optJSONObject("data")
                            if (dataObj != null) {
                                val msg = Gson().fromJson(dataObj.toString(), ChatMessage::class.java)
                                val isFromPartner = msg.sender.id == partner.id
                                val isToPartner = msg.recipient?.id == partner.id
                                val isBroadcastFromDietitian = msg.isBroadcast &&
                                        userInfo?.role == "ROLE_USER" &&
                                        msg.sender.id == userInfo?.dietitian?.id

                                if (isFromPartner || isToPartner || isBroadcastFromDietitian) {
                                    val senderIsMe = msg.sender.id == userInfo?.id
                                    if (senderIsMe) {
                                        val existingOptimistic = chatMessages.find { it.sender.id == userInfo?.id && it.content == msg.content && it.id > 1000000000000L }
                                        if (existingOptimistic != null) {
                                            chatMessages = chatMessages.filter { it.id != existingOptimistic.id } + msg
                                        } else if (chatMessages.none { it.id == msg.id }) {
                                            chatMessages = chatMessages + msg
                                        }
                                    } else {
                                        if (chatMessages.none { it.id == msg.id }) {
                                            chatMessages = chatMessages + msg
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                super.onFailure(webSocket, t, response)
                android.util.Log.e("ExploreViewModel", "WebSocket failure: ${t.localizedMessage}", t)
                this@ExploreViewModel.webSocket = null
                isWebSocketConnected = false
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                this@ExploreViewModel.webSocket = null
                isWebSocketConnected = false
            }
        })
    }

    fun sendChatMessage(content: String) {
        val partner = chatWithUser ?: return
        if (content.isBlank()) return

        val ws = webSocket
        var sentOk = false
        if (ws != null && isWebSocketConnected) {
            val payload = JSONObject().apply {
                put("recipientId", partner.id)
                put("content", content)
                put("isBroadcast", false)
            }
            sentOk = ws.send(payload.toString())
            if (sentOk) {
                // Optimistic updates for messaging feel
                val me = userInfo ?: return
                val optimisticMsg = ChatMessage(
                    id = System.currentTimeMillis(),
                    sender = me,
                    recipient = partner,
                    content = content,
                    isBroadcast = false,
                    isRead = false,
                    sentAt = java.time.LocalDateTime.now().toString()
                )
                chatMessages = chatMessages + optimisticMsg
            } else {
                android.util.Log.w("ExploreViewModel", "WebSocket send returned false, falling back to HTTP REST API.")
            }
        }
        
        if (!sentOk) {
            viewModelScope.launch {
                try {
                    val msg = apiService.sendChatMessage(partner.id, MessageRequest(content))
                    chatMessages = chatMessages + msg
                } catch (e: Exception) {
                    _uiEvent.emit(ExploreUiEvent.Error("Mesaj gönderilemedi: ${e.localizedMessage}"))
                }
            }
        }
    }

    fun endChat() {
        webSocket?.close(1000, "Goodbye")
        webSocket = null
        isWebSocketConnected = false
        chatWithUser = null
        chatMessages = emptyList()
        reloadInitialData()
    }

    override fun onCleared() {
        super.onCleared()
        webSocket?.close(1000, "ViewModel cleared")
    }
}

class ExploreViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExploreViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
