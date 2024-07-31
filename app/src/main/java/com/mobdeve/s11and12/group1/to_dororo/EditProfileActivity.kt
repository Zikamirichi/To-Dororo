package com.mobdeve.s11and12.group1.to_dororo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var editUsername: EditText
    private lateinit var editName: EditText
    private lateinit var saveProfileButton: Button
    private lateinit var heartCountTextView: TextView
    private lateinit var cancel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        editUsername = findViewById(R.id.edit_username)
        editName = findViewById(R.id.edit_name)
        cancel = findViewById(R.id.cancel_edit_profile)
        saveProfileButton = findViewById(R.id.save_profile_button)
        heartCountTextView = findViewById(R.id.heart_count)

        fetchHeartCount()

        saveProfileButton.setOnClickListener {
            saveUserProfile()
        }

        loadUserProfile()

        cancel.setOnClickListener {
            finish()
        }
    }

    private fun loadUserProfile() {
        val user: FirebaseUser? = auth.currentUser
        user?.let {
            val userId = it.uid
            val docRef = db.collection("users").document(userId)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val username = document.getString("username")
                    val name = document.getString("name")
                    editUsername.setText(username)
                    editName.setText(name)
                }
            }
        }
    }

    private fun saveUserProfile() {
        val user: FirebaseUser? = auth.currentUser
        user?.let {
            val userId = it.uid
            val username = editUsername.text.toString()
            val name = editName.text.toString()
            val userProfileUpdates = mapOf(
                "username" to username,
                "name" to name
            )

            db.collection("users").document(userId)
                .update(userProfileUpdates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT).show()
                }
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


