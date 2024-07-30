package com.mobdeve.s11and12.group1.to_dororo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PomodoroTimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro_timer)

        val noteTitle = intent.getStringExtra("NOTE_TITLE")

        if (savedInstanceState == null) {
            val fragment = PomodoroTimerFragment().apply {
                arguments = Bundle().apply {
                    putString("NOTE_TITLE", noteTitle)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}

