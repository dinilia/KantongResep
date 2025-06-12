package com.andiniaulia3119.kantongresep.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.andiniaulia3119.kantongresep.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore : DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(
    name = "user_preference"
)
class UserDataStore(private val context: Context) {

    companion object {
        private val USER_NAME = stringPreferencesKey("name")
        private val USER_EMAIL = stringPreferencesKey("email")
        private val USER_PHOTO = stringPreferencesKey("photoUrl")

    }

    val userFlow: Flow<User> = context.dataStore.data.map {
        User(
            name = it[USER_NAME] ?: "",
            email = it[USER_EMAIL] ?: "",
            photoUrl = it[USER_PHOTO] ?: ""
        )
    }

    suspend fun saveData(user: User) {
        context.dataStore.edit {
            it[USER_NAME] = user.name
            it[USER_EMAIL] = user.email
            it[USER_PHOTO] = user.photoUrl
        }
    }

}