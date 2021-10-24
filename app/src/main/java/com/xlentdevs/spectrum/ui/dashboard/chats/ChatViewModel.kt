package com.xlentdevs.spectrum.ui.dashboard.chats

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.xlentdevs.spectrum.commons.DefaultViewModel
import com.xlentdevs.spectrum.data.db.entity.ChatRoom
import com.xlentdevs.spectrum.data.db.entity.Message
import com.xlentdevs.spectrum.data.db.entity.User
import com.xlentdevs.spectrum.data.db.remote.FirebaseReferenceChildObserver
import com.xlentdevs.spectrum.data.db.remote.FirebaseReferenceValueObserver
import com.xlentdevs.spectrum.data.db.repository.RealTimeDataRepository
import com.xlentdevs.spectrum.data.Result
import com.xlentdevs.spectrum.data.db.entity.Chat
import com.xlentdevs.spectrum.utils.PreferenceStore
import com.xlentdevs.spectrum.utils.addNewItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(
    application: Application,
    var roomId: String,
    var roomName: String,
    var roomPhoto: String,
    var roomDesc: String,
    var roomTag: String
) : DefaultViewModel() {

    private var realTimeDataRepository: RealTimeDataRepository

    private val _addedMessage = MutableLiveData<Message>()

    private val fbRefMessagesChildObserver = FirebaseReferenceChildObserver()
    private val fbRefUserInfoObserver = FirebaseReferenceValueObserver()
    private val firebaseReferenceObserver = FirebaseReferenceValueObserver()

    val messagesList = MediatorLiveData<MutableList<Message>>()
    val newMessageText = MutableLiveData<String?>()

    val userInfo: MutableLiveData<User> = MutableLiveData()
    private var chatID:String = roomId

    private var prefs: PreferenceStore

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        realTimeDataRepository = RealTimeDataRepository(application)
        prefs = PreferenceStore(application)
        observeUserDetails()
        checkAndUpdateLastMessageSeen()
        loadAndObserveNewMessages()
    }

    fun observeUserDetails() {
        uiScope.launch {
            prefs.authToken.collect{ user ->
                userInfo.value = user
            }
        }
    }

    private fun checkAndUpdateLastMessageSeen() {
        realTimeDataRepository.loadChat("chats", chatID) { result: Result<Chat> ->
            if (result is Result.Success && result.data != null) {
                result.data.lastMessage.let {
                    if (!it.seen && it.senderId != userInfo.value!!.uid) {
                        it.seen = true
                        realTimeDataRepository.updateChatLastMessage(chatID, it)
                    }
                }
            }
        }
    }

    private fun loadAndObserveNewMessages() {
        messagesList.addSource(_addedMessage) { messagesList.addNewItem(it) }

        realTimeDataRepository.loadAndObserveMessagesAdded(
            "messages",
            chatID,
            fbRefMessagesChildObserver
        ) { result: Result<Message> ->
            onResult(_addedMessage, result)
        }
    }

    fun sendMessagePressed() {
        if (!newMessageText.value.isNullOrBlank()) {
            val newMsg = Message(userInfo.value!!.uid, newMessageText.value!!)
            realTimeDataRepository.updateNewMessage("messages", chatID, newMsg)
            realTimeDataRepository.updateChatLastMessage(chatID, newMsg)
            newMessageText.value = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        fbRefMessagesChildObserver.clear()
        fbRefUserInfoObserver.clear()
    }
}