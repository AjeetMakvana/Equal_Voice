package com.xlentdevs.spectrum.data.db.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.xlentdevs.spectrum.data.Result
import com.xlentdevs.spectrum.data.model.AuthUser

class FirebaseAuthStateObserver {

    private var authListener: FirebaseAuth.AuthStateListener? = null
    private var instance: FirebaseAuth? = null

    fun start(valueEventListener: FirebaseAuth.AuthStateListener, instance: FirebaseAuth) {
        this.authListener = valueEventListener
        this.instance = instance
        this.instance!!.addAuthStateListener(authListener!!)
    }

    fun clear() {
        authListener?.let { instance?.removeAuthStateListener(it) }
    }
}

class FirebaseAuthSource {

    companion object {
        val authInstance = FirebaseAuth.getInstance()
    }

    private fun attachAuthObserver(b: ((Result<FirebaseUser>) -> Unit)): FirebaseAuth.AuthStateListener {
        return FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                b.invoke(Result.Error("No user"))
            } else { b.invoke(Result.Success(it.currentUser)) }
        }
    }

    fun loginWithEmailAndPassword(login: AuthUser): Task<AuthResult> {
        return authInstance.signInWithEmailAndPassword(login.email, login.password)
    }

    fun createUser(register: AuthUser): Task<AuthResult> {
        return authInstance.createUserWithEmailAndPassword(register.email, register.password)
    }

    fun logout() {
        authInstance.signOut()
    }

    fun attachAuthStateObserver(firebaseAuthStateObserver: FirebaseAuthStateObserver, b: ((Result<FirebaseUser>) -> Unit)) {
        val listener = attachAuthObserver(b)
        firebaseAuthStateObserver.start(listener, authInstance)
    }

    fun googleSignUp(googleAuthCredentials : AuthCredential) : Task<AuthResult>{
        return authInstance.signInWithCredential(googleAuthCredentials)
    }
}
