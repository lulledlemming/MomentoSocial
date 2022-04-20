package com.dandy.momento

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation)
        val loginIntent = Intent(this, SignInActivity::class.java)

        appLogoSplash.startAnimation(splashAnimation)

        val splashScreenTimeOut = 2000

        Handler().postDelayed({
            startActivity(loginIntent)
            finish()
        }, splashScreenTimeOut.toLong())
    }
}