package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var signUpButton: Button
    private lateinit var login: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        signUpButton = findViewById(R.id.sign_up_button)
        login = findViewById(R.id.login_text)

        signUpButton.setOnClickListener {
            // Redirect to MainActivity or any other appropriate activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish current activity after starting MainActivity
        }

        login.setOnClickListener {
            // Redirect to LogInActivity
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish() // Finish current activity after starting LogInActivity
        }
    }
}
