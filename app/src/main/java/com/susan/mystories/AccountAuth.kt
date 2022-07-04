package com.susan.mystories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class AccountAuth @Inject constructor(private val apiStories: ApiStories) {

    private val _loadStories = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _loadStories
    private val _responseAccount = MutableLiveData<Boolean>()
    val responAccount: LiveData<Boolean> = _responseAccount
    private val _signInStories = MutableLiveData<SignInStoriesResult>()
    val signInStories: LiveData<SignInStoriesResult> = _signInStories

    fun signUpStoriesAccount(signUp : ModelSignUp) {
        _loadStories.value=true
        val apiStories = apiStories.accountStoriesCreated(signUp)
        apiStories.enqueue(object : Callback<ResponseSignUpStories> {
            override fun onResponse(
                call: Call<ResponseSignUpStories>,
                response: Response<ResponseSignUpStories>
            ) {
                _loadStories.value=false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _responseAccount.value = true

                    Log.d("ResponseAccount", "onResponse: ${responseBody.message}")
                } else {
                    _responseAccount.value = false
                }
            }

            override fun onFailure(call: Call<ResponseSignUpStories>, t: Throwable) {
                _loadStories.value=false
                Log.d("ResponseAccount", "onFailure: ${t.message}")
            }

        })
    }

    fun signInStoriesAccount(signIn: ModelSignInStories) {
        _loadStories.value=true
        val apiStories = apiStories.accountStoriesSignIn(signIn)
        apiStories.enqueue(object : Callback<ResponseSignInStories> {
            override fun onResponse(call: Call<ResponseSignInStories>, response: Response<ResponseSignInStories>) {
                _loadStories.value=false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _responseAccount.value = true

                    Log.d("ResponseSuccess", "onResponse: ${responseBody.loginResult}")
                    _signInStories.value = responseBody.loginResult
                } else {
                    _responseAccount.value = false
                }
            }

            override fun onFailure(call: Call<ResponseSignInStories>, t: Throwable) {
                _loadStories.value=false
                Log.e("ResponseError", "onFailure: ${t.message}")
            }
        })
    }
}