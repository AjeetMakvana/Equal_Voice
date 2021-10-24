package com.xlentdevs.spectrum.commons

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xlentdevs.spectrum.data.Result

abstract class DefaultViewModel: ViewModel() {

    val snackBarText = MutableLiveData<String>()
    val dataLoading = MutableLiveData<Boolean>()

    protected fun <T> onResult(mutableLiveData: MutableLiveData<T>? = null, result: Result<T>) {
        when (result) {
            is Result.Loading -> dataLoading.value = true

            is Result.Error -> {
                dataLoading.value = false
                result.msg?.let { snackBarText.value = it }
            }

            is Result.Success -> {
                dataLoading.value = false
                result.data?.let { mutableLiveData?.value = it }
                result.msg?.let { snackBarText.value = it }
            }
        }
    }
}