package com.xlentdevs.spectrum.ui.dashboard.chatroom

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xlentdevs.spectrum.ui.dashboard.discover.DiscoverViewModel

class ChatRoomViewModelFactory(
    private val application: Application
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatRoomViewModel(application) as T
    }
}