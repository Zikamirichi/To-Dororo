package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class VirtualPet_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navbar)
        bottomNavigationView.selectedItemId = R.id.virtualPet

        val options = ActivityOptionsCompat.makeCustomAnimation(
            applicationContext,
            R.anim.slide_in_right, // Enter animation
            R.anim.slide_out_left // Exit animation
        )

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.virtualPet -> {
                    true // Return true to indicate item selection handled
                }
                R.id.toDo -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java), options.toBundle())
                    finish()
                    true // Return true to indicate item selection handled
                }
                R.id.userProfile -> {
                    startActivity(Intent(applicationContext, UserProfile_Activity::class.java), options.toBundle())
                    finish()
                    true // Return true to indicate item selection handled
                }
                R.id.pomodoro -> {
                    startActivity(Intent(applicationContext, PomodoroTimer_Activity::class.java), options.toBundle())
                    finish()
                    true // Return true to indicate item selection handled
                }
                else -> false // Return false for items not handled
            }
        }
    }

}