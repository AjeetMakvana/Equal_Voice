package com.xlentdevs.spectrum.data.db.repository

import android.app.Application
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.xlentdevs.spectrum.data.db.remote.FirebaseAuthSource
import com.xlentdevs.spectrum.data.Result
import com.xlentdevs.spectrum.data.db.remote.FirebaseAuthStateObserver
import com.xlentdevs.spectrum.data.model.AuthUser

class AuthAppRepository(private var application: Application?) {

    private val firebaseAuthService = FirebaseAuthSource()

    fun observeAuthState(
        stateObserver: FirebaseAuthStateObserver,
        b: ((Result<FirebaseUser>) -> Unit)
    ) {
        firebaseAuthService.attachAuthStateObserver(stateObserver, b)
    }

    fun loginUser(login: AuthUser, b: ((Result<FirebaseUser>) -> Unit)) {
        b.invoke(Result.Loading)
        firebaseAuthService.loginWithEmailAndPassword(login).addOnSuccessListener {
            b.invoke(Result.Success(it.user, msg = "Success"))
        }.addOnFailureListener {
            b.invoke(Result.Error(msg = it.message))
        }
    }

    fun createUser(register: AuthUser, b: ((Result<FirebaseUser>) -> Unit)) {

        b.invoke(Result.Loading)

        firebaseAuthService.createUser(register).addOnSuccessListener {
            b.invoke(Result.Success(it.user, msg = "Success"))
        }.addOnFailureListener {
            b.invoke(Result.Error(msg = it.message))
        }
    }

    fun logoutUser() {
        firebaseAuthService.logout()
    }

    // Sign in using google
    fun firebaseSignInWithGoogle(
        googleAuthCredential: AuthCredential,
        b: ((Result<FirebaseUser>) -> Unit)
    ) {

        b.invoke(Result.Loading)

        firebaseAuthService.googleSignUp(googleAuthCredential).addOnSuccessListener {
            b.invoke(Result.Success(it.user, "Success"))
        }.addOnFailureListener {
            b.invoke(Result.Error(msg = it.message))
        }
    }
}