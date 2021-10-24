package com.xlentdevs.spectrum.ui.dashboard.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xlentdevs.spectrum.ui.dashboard.discover.DiscoverViewModel

class HomeViewModelFactory(
    private val application: Application
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(application) as T
    }
}