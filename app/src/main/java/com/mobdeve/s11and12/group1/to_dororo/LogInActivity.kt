package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LogInActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var createAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.login_button)
        createAccount = findViewById(R.id.create_account)

        loginButton.setOnClickListener {
            // Redirect to MainActivity or any other appropriate activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish current activity after starting MainActivity
        }

        createAccount.setOnClickListener {
            // Redirect to CreateAccountActivity
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }
}
