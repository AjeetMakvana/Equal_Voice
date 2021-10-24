package com.xlentdevs.spectrum.ui.dashboard.chats

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xlentdevs.spectrum.data.db.entity.ChatRoom

class ChatViewModelFactory(
    private val application: Application,
    private val roomUid: String,
    private val roomName: String,
    private val roomPhoto: String,
    private val roomDesc: String,
    private val roomTag: String
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(application, roomUid, roomName, roomPhoto, roomDesc, roomTag) as T
    }

}