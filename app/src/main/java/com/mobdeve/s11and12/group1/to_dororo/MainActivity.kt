package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navbar)
        bottomNavigationView.selectedItemId = R.id.toDo

        val options = ActivityOptionsCompat.makeCustomAnimation(
            applicationContext,
            R.anim.slide_in_right, // Enter animation
            R.anim.slide_out_left // Exit animation
        )

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.toDo -> {
                    true // Return true to indicate item selection handled
                }
                R.id.pomodoro -> {
                    startActivity(Intent(applicationContext, PomodoroTimer_Activity::class.java), options.toBundle())
                    finish()
                    true // Return true to indicate item selection handled
                }
                R.id.virtualPet -> {
                    startActivity(Intent(applicationContext, VirtualPet_Activity::class.java), options.toBundle())
                    finish()
                    true // Return true to indicate item selection handled
                }
                R.id.userProfile -> {
                    startActivity(Intent(applicationContext, UserProfile_Activity::class.java), options.toBundle())
                    finish()
                    true // Return true to indicate item selection handled
                }
                else -> false // Return false for items not handled
            }
        }
    }

}
