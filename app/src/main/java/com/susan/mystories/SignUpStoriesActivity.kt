package com.susan.mystories

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.susan.mystories.databinding.ActivitySignUpStoriesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpStoriesActivity : AppCompatActivity() {

    private val storiesViewModel by viewModels<StoriesViewModel>()
    private lateinit var activitySignUpStoriesBinding: ActivitySignUpStoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignUpStoriesBinding = ActivitySignUpStoriesBinding.inflate(layoutInflater)
        setContentView(activitySignUpStoriesBinding.root)
        supportActionBar?.hide()
        storiesViewModel.responAccount.observe(this) {
            if (!it) {
                Toast.makeText(this, R.string.fill_form, Toast.LENGTH_SHORT).show()
            }
        }
        storiesViewModel.showLoading.observe(this) {
            showLoading(it)
        }
        accountToken()
        aturTampilan()
        storiesAnimation()
        validateButton()
    }

    private fun accountToken() {
        storiesViewModel.getAccountStories().observe(this) {
            if(it.token.trim() != "") {
                val intent = Intent(this@SignUpStoriesActivity, ViewStoriesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun akunSignUp() {
        val accountName = activitySignUpStoriesBinding.nameAccount.text.toString().trim()
        val accountEmail = activitySignUpStoriesBinding.emailAccount.text.toString().trim()
        val accountPassword = activitySignUpStoriesBinding.passwordAccount.text.toString().trim()
        when {
            accountName.isEmpty() -> {
                activitySignUpStoriesBinding.nameAccount.error = getString(R.string.name_account)
            }

            accountEmail.isEmpty() -> {
                activitySignUpStoriesBinding.emailAccount.error = getString(R.string.email_account)
            }
            accountPassword.isEmpty() -> {
                activitySignUpStoriesBinding.passwordAccount.error = getString(R.string.password_account)
            }
            else -> {
                if (validateEmail(accountEmail)) {
                    storiesViewModel.signUpStories(ModelSignUp(accountName, accountEmail, accountPassword))
                    Toast.makeText(this, R.string.success_signup, Toast.LENGTH_SHORT)
                        .show()
                    val mainIntent =
                        Intent(this@SignUpStoriesActivity, SignInStoriesActivity::class.java)
                    startActivity(mainIntent)
                } else {
                    Toast.makeText(this, R.string.failed_signup, Toast.LENGTH_SHORT)
                        .show()
                }

                storiesViewModel.signInStories.observe(this) {
                    if (it.token != "") {
                        storiesViewModel.accountStoriesSave(it)
                    }

                }
            }
        }
    }

    private fun validateEmail(email : String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateButton() {
        activitySignUpStoriesBinding.apply {
            btnSignUp.setOnClickListener{
                akunSignUp()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            activitySignUpStoriesBinding.progressBar.visibility = View.VISIBLE
        } else {
            activitySignUpStoriesBinding.progressBar.visibility = View.GONE
        }
    }

    private fun aturTampilan() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun storiesAnimation() {
        ObjectAnimator.ofFloat(activitySignUpStoriesBinding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(activitySignUpStoriesBinding.title, View.ALPHA, 1f).setDuration(500)
        val nameAccount = ObjectAnimator.ofFloat(activitySignUpStoriesBinding.nameAccount, View.ALPHA, 1f).setDuration(500)
        val emailAccount = ObjectAnimator.ofFloat(activitySignUpStoriesBinding.emailAccount, View.ALPHA, 1f).setDuration(500)
        val passwordAccount = ObjectAnimator.ofFloat(activitySignUpStoriesBinding.passwordAccount, View.ALPHA, 1f).setDuration(500)
        val btnSignUp = ObjectAnimator.ofFloat(activitySignUpStoriesBinding.btnSignUp, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            play(title)
            play(nameAccount).after(title)
            play(emailAccount).after(nameAccount)
            play(passwordAccount).after(emailAccount)
            play(btnSignUp).after(passwordAccount)
            start()
        }
    }
}