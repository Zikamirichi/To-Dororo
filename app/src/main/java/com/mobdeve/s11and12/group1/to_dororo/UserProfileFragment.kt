package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var usernameDisplay: TextView
    private lateinit var nameDisplay: TextView
    private lateinit var emailDisplay: TextView
    private lateinit var changePasswordText: TextView
    private lateinit var logoutButton: Button
    private lateinit var editProfileButton: TextView
    private lateinit var welcomeTextView: TextView
    private lateinit var heartCountTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        usernameDisplay = view.findViewById(R.id.username_profile)
        nameDisplay = view.findViewById(R.id.name_profile)
        emailDisplay = view.findViewById(R.id.email_profile)
        changePasswordText = view.findViewById(R.id.change_password)
        logoutButton = view.findViewById(R.id.logout_button)
        editProfileButton = view.findViewById(R.id.edit_profile)
        welcomeTextView = view.findViewById(R.id.welcome)
        heartCountTextView = view.findViewById(R.id.heart_count)

        fetchHeartCount()
        loadUserProfile()

        editProfileButton.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        changePasswordText.setOnClickListener {
            val intent = Intent(activity, ChangePassActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, LogInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
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
                    val email = document.getString("email")
                    usernameDisplay.text = username
                    nameDisplay.text = name
                    emailDisplay.text = email
                    welcomeTextView.text = "Hello, $name"
                }
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
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




