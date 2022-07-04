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
import com.susan.mystories.databinding.ActivitySignInStoriesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInStoriesActivity : AppCompatActivity() {

    private val storiesViewModel by viewModels<StoriesViewModel>()
    private lateinit var activitySignInStoriesBinding: ActivitySignInStoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignInStoriesBinding = ActivitySignInStoriesBinding.inflate(layoutInflater)
        setContentView(activitySignInStoriesBinding.root)
        supportActionBar?.hide()
        storiesViewModel.responAccount.observe(this) {
            if (!it) {
                Toast.makeText(this, R.string.failed_signin, Toast.LENGTH_SHORT).show()
            }
        }
        storiesViewModel.showLoading.observe(this) {
            showLoading(it)
        }
        validateButton()
        akunSignIn()
        aturTampilan()
        storiesAnimation()
    }

    private fun validateButton() {
        activitySignInStoriesBinding.apply {
            btnSignIn.setOnClickListener {
                startSignIn()
            }
            btnSignUp.setOnClickListener {
                startActivity(Intent(this@SignInStoriesActivity, SignUpStoriesActivity::class.java))
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            activitySignInStoriesBinding.progressBar.visibility = View.VISIBLE
        } else {
            activitySignInStoriesBinding.progressBar.visibility = View.GONE
        }
    }

    private fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun startSignIn() {
        val accountEmail = activitySignInStoriesBinding.emailAccount.text.toString()
        val accountPassword = activitySignInStoriesBinding.passwordAccount.text.toString()

        when {
            accountEmail.isEmpty() -> {
                activitySignInStoriesBinding.emailAccount.error = getString(R.string.email_account)
            }
            accountPassword.isEmpty() -> {
                activitySignInStoriesBinding.passwordAccount.error = getString(R.string.password_account)
            }
            else -> {
                if(validateEmail(accountEmail)) {
                    storiesViewModel.signInStories(ModelSignInStories(accountEmail, accountPassword))
                } else {
                    Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show()
                }

                storiesViewModel.signInStories.observe(this) {
                    storiesViewModel.accountStoriesSave(it)
                }
            }
        }
    }

    private fun akunSignIn() {
        storiesViewModel.getAccountStories().observe(this) {
            if (it.token.trim() != "") {
                val intent = Intent(this@SignInStoriesActivity, ViewStoriesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
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
        ObjectAnimator.ofFloat(activitySignInStoriesBinding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(activitySignInStoriesBinding.title, View.ALPHA, 1f).setDuration(500)
        val emailAccount = ObjectAnimator.ofFloat(activitySignInStoriesBinding.emailAccount, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(activitySignInStoriesBinding.passwordAccount, View.ALPHA, 1f).setDuration(500)
        val btnSignIn = ObjectAnimator.ofFloat(activitySignInStoriesBinding.btnSignIn, View.ALPHA, 1f).setDuration(500)
        val textView = ObjectAnimator.ofFloat(activitySignInStoriesBinding.textView, View.ALPHA, 1f).setDuration(500)
        val btnSignUp = ObjectAnimator.ofFloat(activitySignInStoriesBinding.btnSignUp, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            play(title)
            play(emailAccount).after(title)
            play(password).after(emailAccount)
            play(btnSignIn).after(password)
            play(textView).after(btnSignIn)
            play(btnSignUp).after(textView)
            start()
        }
    }
}