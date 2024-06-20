package com.mobdeve.s11and12.group1.to_dororo

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        // Slide in animation
        val slideInAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        val mainLayout = findViewById<View>(R.id.main)
        mainLayout.startAnimation(slideInAnimation)

        // Make the activity content visible after the animation ends
        slideInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                mainLayout.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }
}
