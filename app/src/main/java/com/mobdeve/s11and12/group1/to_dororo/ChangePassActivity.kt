package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ChangePassActivity : AppCompatActivity() {
    private lateinit var savePassButton: Button
    private lateinit var cancel1: TextView
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var heartCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_profile)

        savePassButton = findViewById(R.id.save_password_button)
        cancel1 = findViewById(R.id.cancel_edit_profile)
        oldPassword = findViewById(R.id.old_password)
        newPassword = findViewById(R.id.new_password)
        confirmPassword = findViewById(R.id.confirm_password)
        heartCountTextView = findViewById(R.id.heart_count)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        fetchHeartCount()

        savePassButton.setOnClickListener {
            changePassword()
        }

        cancel1.setOnClickListener {
            finish()
        }

        // Adding helpView and historyButton functionality
        val helpView = findViewById<ImageButton>(R.id.help_icon)
        helpView.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }

        val historyButton = findViewById<ImageButton>(R.id.history_icon)
        historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun changePassword() {
        val user: FirebaseUser? = auth.currentUser
        val oldPass = oldPassword.text.toString()
        val newPass = newPassword.text.toString()
        val confirmPass = confirmPassword.text.toString()

        if (user != null && newPass == confirmPass) {
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPass)

            // Re-authenticate the user
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    // User has been re-authenticated, proceed to update password
                    user.updatePassword(newPass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password Updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Password Update Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Old Password is incorrect", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Passwords do not match or user is null", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchHeartCount() {
        val user: FirebaseUser? = auth.currentUser
        user?.let {
            val userId = it.uid

            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val heartCount = document.getLong("hearts") ?: 0
                    heartCountTextView.text = heartCount.toString()
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    exception.printStackTrace()
                }
        }
    }
}
