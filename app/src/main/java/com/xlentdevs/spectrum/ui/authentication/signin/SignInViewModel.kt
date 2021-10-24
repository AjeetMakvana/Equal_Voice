package com.xlentdevs.spectrum.ui.authentication.signin

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.xlentdevs.spectrum.commons.DefaultViewModel
import com.xlentdevs.spectrum.data.db.remote.FirebaseReferenceValueObserver
import com.xlentdevs.spectrum.data.db.repository.AuthAppRepository
import com.xlentdevs.spectrum.data.db.repository.RealTimeDataRepository
import com.xlentdevs.spectrum.data.model.AuthUser
import com.xlentdevs.spectrum.utils.PreferenceStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.xlentdevs.spectrum.data.Result
import com.xlentdevs.spectrum.data.db.entity.User
import com.xlentdevs.spectrum.utils.isEmailValid
import com.xlentdevs.spectrum.utils.isTextValid

class SignInViewModel(
    application: Application
): DefaultViewModel() {

    private var authAppRepository: AuthAppRepository
    private var realTimeDataRepository: RealTimeDataRepository
    var prefs: PreferenceStore
    private val firebaseReferenceObserver = FirebaseReferenceValueObserver()

    //For Login Flow
    val isLogingInAccount = MutableLiveData<Boolean>()
    val isLoggedIn = MutableLiveData<FirebaseUser?>()

    //Two way Binding with edit text of Login page
    val emailTextLogin = MutableLiveData<String>()
    val passwordTextLogin = MutableLiveData<String>()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        authAppRepository = AuthAppRepository(application)
        realTimeDataRepository = RealTimeDataRepository(application)
        prefs = PreferenceStore(application)
    }

    //------------------------------Email & Password Login code starts------------------------------
    fun loginAccountPressed() {
        if (!isEmailValid(emailTextLogin.value.toString())) {
            snackBarText.value = "Invalid email format"
            return
        }

        if (!isTextValid(6, passwordTextLogin.value)) {
            snackBarText.value = "Password is too short"
            return
        }

        loginAccount()
    }

    private fun loginAccount() {
        isLogingInAccount.value = true

        val loginUser = AuthUser(emailTextLogin.value!!, passwordTextLogin.value!!)

        loginUser(loginUser)
    }

    private fun loginUser(loginUser: AuthUser) {
        authAppRepository.loginUser(loginUser)
        { result: Result<FirebaseUser> ->
            onResult(null, result)
            if (result is Result.Success) {

                realTimeDataRepository.loadAndObserveUserInfo(
                    "users",
                    result.data!!.uid,
                    firebaseReferenceObserver
                )
                { result1: Result<User> ->
                    onResult(null, result1)
                    if (result1 is Result.Success) {
                        uiScope.launch {
                            prefs.saveAuthToken(
                                result1.data!!.uid,
                                result1.data.name,
                                result1.data.email,
                                result1.data.profile
                            )
                        }
                    }
                    isLoggedIn.value = result.data
                }

            }
            if (result is Result.Success || result is Result.Error) isLogingInAccount.value = false
        }
    }
    //-----------------------------Email & Password Login code ends---------------------------------

    //Google Login
    fun googleLogin(googleAuthCredential: AuthCredential) {
        authAppRepository.firebaseSignInWithGoogle(googleAuthCredential)
        { result: Result<FirebaseUser> ->
            onResult(null, result)
            if (result is Result.Success) {

                uiScope.launch {
                    realTimeDataRepository.loadAndObserveUserInfo(
                        "users",
                        result.data!!.uid,
                        firebaseReferenceObserver
                    )
                    { result1: Result<User> ->
                        onResult(null, result1)

                        if (result1 is Result.Success) {
                            uiScope.launch {
                                prefs.saveAuthToken(
                                    result1.data!!.uid,
                                    result1.data.name,
                                    result1.data.email,
                                    result1.data.profile
                                )
                            }
                        }

                        isLoggedIn.value = result.data
                    }
                }
            }
        }
    }
}