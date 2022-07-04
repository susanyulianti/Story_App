package com.susan.mystories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStoreAppStories: DataStore<Preferences> by preferencesDataStore(name = "storiesAccount")
class PreferencesStoriesAccount @Inject constructor(@ApplicationContext val context: Context){

    private val dataStoreAppStories = context.dataStoreAppStories

    suspend fun storiesAccountSave(storiesSignIn:SignInStoriesResult){
        setStoriesIdlingResource {
            dataStoreAppStories.edit { preferences ->
                preferences[NAME_ACCOUNT_SORIES_KEY] = storiesSignIn.name
                preferences[ID_ACCOUNT_SORIES_KEY] = storiesSignIn.userId
                preferences[TOKEN_ACCOUNT_SORIES_KEY] = storiesSignIn.token
            }
        }
    }

    fun storiesAccountGet(): Flow<SignInStoriesResult>{
        return dataStoreAppStories.data.map { preferences ->
            SignInStoriesResult(
                preferences[NAME_ACCOUNT_SORIES_KEY]?:"",
                preferences[ID_ACCOUNT_SORIES_KEY]?:"",
                preferences[TOKEN_ACCOUNT_SORIES_KEY]?:""
            )
        }
    }

    suspend fun signOutStoriesApp(){
        dataStoreAppStories.edit {
            it.clear()
        }
    }

    companion object {
        private val NAME_ACCOUNT_SORIES_KEY = stringPreferencesKey("name")
        private val ID_ACCOUNT_SORIES_KEY = stringPreferencesKey("userid")
        private val TOKEN_ACCOUNT_SORIES_KEY = stringPreferencesKey("token")
    }
}