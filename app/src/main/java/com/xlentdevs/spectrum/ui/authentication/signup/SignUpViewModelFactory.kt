package com.xlentdevs.spectrum.ui.authentication.signup

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class SignUpViewModelFactory(
    private val application: Application
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)){
            return SignUpViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel !")
    }
}