package com.xlentdevs.spectrum.ui.dashboard.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.xlentdevs.spectrum.commons.DefaultViewModel
import com.xlentdevs.spectrum.data.db.entity.ChatRoom
import com.xlentdevs.spectrum.data.db.entity.User
import com.xlentdevs.spectrum.data.db.remote.FirebaseReferenceValueObserver
import com.xlentdevs.spectrum.data.db.repository.RealTimeDataRepository
import com.xlentdevs.spectrum.utils.PreferenceStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application
): DefaultViewModel() {

    private var realTimeDataRepository: RealTimeDataRepository
    var prefs: PreferenceStore
    private val firebaseReferenceObserver = FirebaseReferenceValueObserver()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val userInfo: MutableLiveData<User> = MutableLiveData()

    init {
        realTimeDataRepository = RealTimeDataRepository(application)
        prefs = PreferenceStore(application)
        observeUserDetails()
    }

    fun observeUserDetails() {
        uiScope.launch {
            prefs.authToken.collect { user ->
                userInfo.value = user
            }
        }
    }
}