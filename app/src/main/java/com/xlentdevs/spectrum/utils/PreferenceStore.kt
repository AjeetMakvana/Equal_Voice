package com.xlentdevs.spectrum.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.xlentdevs.spectrum.data.db.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferenceStore(val context: Context) {

    private val applicationContext = context.applicationContext

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "USERDATA")
        private val USERID = stringPreferencesKey("userId")
        private val NAME = stringPreferencesKey("name")
        private val EMAIL = stringPreferencesKey("email")
        private val PROFILE = stringPreferencesKey("profile")
    }

    //Saving the user uid
    suspend fun saveAuthToken(id: String, name: String, email: String, profile: String) {
        applicationContext.dataStore.edit { preference ->
            preference[USERID] = id
            preference[NAME] = name
            preference[EMAIL] = email
            preference[PROFILE] = profile
        }
    }

    //retreiving the user uid
    val authToken: Flow<User?> =
        applicationContext.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preference ->
                val id = preference[USERID] ?: "NA"
                val name = preference[NAME] ?: "NA"
                val email = preference[EMAIL] ?: "NA"
                val profile = preference[PROFILE] ?: "NA"
                User(id, name, email, profile)
            }
}