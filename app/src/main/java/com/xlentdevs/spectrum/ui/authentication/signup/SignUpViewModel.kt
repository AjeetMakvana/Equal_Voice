package com.xlentdevs.spectrum.ui.authentication.signup

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.xlentdevs.spectrum.commons.DefaultViewModel
import com.xlentdevs.spectrum.data.Result
import com.xlentdevs.spectrum.data.db.entity.User
import com.xlentdevs.spectrum.data.db.repository.AuthAppRepository
import com.xlentdevs.spectrum.data.db.repository.RealTimeDataRepository
import com.xlentdevs.spectrum.data.model.AuthUser
import com.xlentdevs.spectrum.utils.PreferenceStore
import com.xlentdevs.spectrum.utils.isEmailValid
import com.xlentdevs.spectrum.utils.isTextValid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SignUpViewModel(
    application: Application
) : DefaultViewModel() {

    private var authAppRepository: AuthAppRepository
    private var realTimeDataRepository: RealTimeDataRepository
    var prefs: PreferenceStore

    //For SignUp Flow
    val isCreatingAccount = MutableLiveData<Boolean>()
    val isRegistered = MutableLiveData<FirebaseUser?>()

    //Two way Binding with edit text of SignUp page
    val emailTextSignUp = MutableLiveData<String>()
    val passwordTextSignUp = MutableLiveData<String>()
    val nameTextSignUp = MutableLiveData<String>()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        authAppRepository = AuthAppRepository(application)
        realTimeDataRepository = RealTimeDataRepository(application)
        prefs = PreferenceStore(application)
    }

    //------------------------------Email & Password Sign Up code starts----------------------------
    fun createAccountPressed() {
        if (!isEmailValid(emailTextSignUp.value.toString())) {
            snackBarText.value = "Invalid email format"
            return
        }

        if (!isTextValid(6, passwordTextSignUp.value)) {
            snackBarText.value = "Password is too short"
            return
        }

        createAccount()
    }

    private fun createAccount() {
        isCreatingAccount.value = true

        val createUser = AuthUser(emailTextSignUp.value!!, passwordTextSignUp.value!!)

        registerUser(createUser)
    }

    private fun registerUser(createUser: AuthUser) {
        authAppRepository.createUser(createUser)
        { result: Result<FirebaseUser> ->

            onResult(null, result)
            if (result is Result.Success) {
                uiScope.launch {
                    prefs.saveAuthToken(
                        result.data?.uid!!,
                        nameTextSignUp.value.toString(),
                        createUser.email,
                        ""
                    )

                    realTimeDataRepository.updateNewUser("users", User().apply {
                        uid = result.data.uid
                        name = nameTextSignUp.value.toString()
                        email = createUser.email
                        profile = ""
                    })
                    isRegistered.value = result.data
                }
            }

            if (result is Result.Success || result is Result.Error) isCreatingAccount.value = false
        }
    }

    //------------------------------Email & Password Sign Up code ends------------------------------

    //Google Sign Up
    fun googleSignUp(googleAuthCredential: AuthCredential) {
        authAppRepository.firebaseSignInWithGoogle(googleAuthCredential)
        { result: Result<FirebaseUser> ->
            onResult(null, result)
            if (result is Result.Success) {
                uiScope.launch {
                    prefs.saveAuthToken(
                        result.data?.uid!!,
                        result.data.displayName!!,
                        result.data.email!!,
                        result.data.photoUrl.toString()
                    )
                    isRegistered.value = result.data
                    realTimeDataRepository.updateNewUser("users", User().apply {
                        uid = result.data.uid
                        name = result.data.displayName!!
                        email = result.data.email!!
                        profile = result.data.photoUrl.toString()
                    })
                }
            }
        }
    }
}