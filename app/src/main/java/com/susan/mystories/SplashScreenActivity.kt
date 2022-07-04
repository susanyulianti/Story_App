package com.susan.mystories

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.susan.mystories.databinding.ActivitySplashScreenBinding

class SplashScreenActivity: AppCompatActivity() {

    private lateinit var activitySplashScreenBinding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySplashScreenBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(activitySplashScreenBinding.root)
        supportActionBar?.hide()
        storiesAnimation()
        activitySplashScreenBinding.btnStart.setOnClickListener {
            val view = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(view)
        }
    }

    private fun storiesAnimation() {
        val title = ObjectAnimator.ofFloat(activitySplashScreenBinding.title, View.ALPHA, 1f).setDuration(1000)
        val title2 = ObjectAnimator.ofFloat(activitySplashScreenBinding.title2, View.ALPHA, 1f).setDuration(1000)
        val button = ObjectAnimator.ofFloat(activitySplashScreenBinding.btnStart, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            play(title)
            play(title2).after(title)
            play(button).after(title2)
            start()
        }
    }
}