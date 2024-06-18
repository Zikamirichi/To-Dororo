package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.navbar)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.toDo -> {
                    Toast.makeText(this, "To-Do", Toast.LENGTH_SHORT).show()
                    replaceFragment(ToDoListFragment())
                    true
                }
                R.id.pomodoro -> {
                    Toast.makeText(this, "Pomodoro", Toast.LENGTH_SHORT).show()
                    replaceFragment(PomodoroTimerFragment())
                    true
                }
                R.id.virtualPet -> {
                    Toast.makeText(this, "Virtual Pet", Toast.LENGTH_SHORT).show()
                    replaceFragment(VirtualPetFragment())
                    true
                }
                R.id.userProfile -> {
                    Toast.makeText(this, "User Profile", Toast.LENGTH_SHORT).show()
                    replaceFragment(UserProfileFragment())
                    true
                }
                else -> false
            }
        }
        replaceFragment(ToDoListFragment())

    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
    }

}
