package com.susan.mystories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoriesViewModel @Inject constructor(private val accountAuth: AccountAuth, private val preferencesStoriesAccount: PreferencesStoriesAccount) : ViewModel() {

    val responAccount: LiveData<Boolean> = accountAuth.responAccount
    val showLoading: LiveData<Boolean> = accountAuth.showLoading
    val signInStories: LiveData<SignInStoriesResult> = accountAuth.signInStories

    fun signUpStories(signUp: ModelSignUp) {
        accountAuth.signUpStoriesAccount(signUp)
    }

    fun signInStories(signIn : ModelSignInStories) {
        accountAuth.signInStoriesAccount(signIn)
    }

    fun getAccountStories(): LiveData<SignInStoriesResult>{
        return preferencesStoriesAccount.storiesAccountGet().asLiveData()
    }

    fun accountStoriesSave(signInStories: SignInStoriesResult){
        viewModelScope.launch {
            preferencesStoriesAccount.storiesAccountSave(SignInStoriesResult(signInStories.name, signInStories.userId, signInStories.token))
        }
    }

    fun signOutStories() {
        viewModelScope.launch {
            preferencesStoriesAccount.signOutStoriesApp()
        }
    }
}