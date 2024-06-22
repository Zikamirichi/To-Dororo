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
            // Perform save actions here if needed
            // Example: saveChanges()

            // Finish this activity to return to UserProfileFragment
            finish()

        }

        cancel1.setOnClickListener {
            finish()
        }
    }

    // DON'T NEED THIS
//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.user_profile, fragment)
//            .commit()
//    }
}
