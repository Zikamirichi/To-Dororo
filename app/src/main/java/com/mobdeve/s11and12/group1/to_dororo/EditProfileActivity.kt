package com.mobdeve.s11and12.group1.to_dororo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class EditProfileActivity : AppCompatActivity() {
    private lateinit var saveInfoButton: Button
    private lateinit var cancel1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        saveInfoButton = findViewById(R.id.save_profile_button)
        cancel1 = findViewById(R.id.cancel_edit_profile)

        saveInfoButton.setOnClickListener {
            replaceFragment(UserProfileFragment())
        }

        cancel1.setOnClickListener {
            replaceFragment(UserProfileFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.user_profile, fragment)
            .commit()
    }
}
