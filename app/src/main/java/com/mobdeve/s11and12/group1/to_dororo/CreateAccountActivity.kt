package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var signUpButton: Button
    private lateinit var login: TextView
    private lateinit var nameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        signUpButton = findViewById(R.id.sign_up_button)
        login = findViewById(R.id.login_text)
        nameEditText = findViewById(R.id.name)
        usernameEditText = findViewById(R.id.username)
        emailEditText = findViewById(R.id.Email)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.confirm_password)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (password == confirmPassword) {
                registerUser(name, username, email, password)
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }

        login.setOnClickListener {
            // Redirect to LogInActivity
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish() // Finish current activity after starting LogInActivity
        }
    }

    private fun registerUser(name: String, username: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val userId = user?.uid

                    val userMap = hashMapOf(
                        "name" to name,
                        "username" to username,
                        "email" to email,
                        "hearts" to 0 // Default value for hearts
                    )

                    userId?.let {
                        firestore.collection("users").document(it).set(userMap)
                            .addOnSuccessListener {
                                // Creating subcollections
                                createDefaultSubcollections(userId)
                                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to register user in Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("FirestoreError", "Failed to register user in Firestore", e)
                            }
                    } ?: run {
                        Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AuthError", "Registration failed", task.exception)
                }
            }
    }

    private fun createDefaultSubcollections(userId: String) {
        val date = Date()
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        val historyMap = hashMapOf(
            "body" to "",
            "date" to "",
            "title" to "",
            "totalTime" to ""
        )

        val notesMap = hashMapOf(
            "body" to "",
            "date" to formattedDate,
            "isCompleted" to false,
            "title" to "To Do Title",
            "totalTime" to "Total: N/A",
            "isSelectedForPomodoro" to false
        )

//        val petsMap = hashMapOf(
//            "heartCount" to "",
//            "stage" to "",
//            "type" to "",
//            "unlocked" to false
//        )

//        val galleryMap = hashMapOf(
//            "heartCount" to "",
//            "stage" to "",
//            "type" to "",
//            "unlocked" to false
//        )

        val petShopItems = listOf(
            PetShopItem("Cat", R.drawable.cat_petshop, 1000),
            PetShopItem("Dog", R.drawable.dog_petshop, 1000),
            PetShopItem("Parrot", R.drawable.parrot_petshop, 1000),
            PetShopItem("Coming Soon", R.drawable.comingsoon_petshop, 1000)
        )

        val galleryItems = listOf(
            PetGalleryItem("Cat", R.drawable.locked_petgallery, R.drawable.locked_petgallery, R.drawable.locked_petgallery),
            PetGalleryItem("Dog", R.drawable.locked_petgallery, R.drawable.locked_petgallery, R.drawable.locked_petgallery),
            PetGalleryItem("Parrot", R.drawable.locked_petgallery, R.drawable.locked_petgallery, R.drawable.locked_petgallery)
        )

        val userDocRef = firestore.collection("users").document(userId)

        userDocRef.collection("history").add(historyMap)
            .addOnFailureListener { e -> Log.e("FirestoreError", "Failed to create history subcollection: ${e.message}", e) }

        userDocRef.collection("notes").add(notesMap)
            .addOnFailureListener { e -> Log.e("FirestoreError", "Failed to create notes subcollection: ${e.message}", e) }

//        userDocRef.collection("pets").add(petsMap)
//            .addOnFailureListener { e -> Log.e("FirestoreError", "Failed to create pets subcollection: ${e.message}", e) }

        // Create pets subcollection
        petShopItems.forEach { item ->
            val petShopItem = hashMapOf(
                "type" to item.type,
                "imageResId" to item.imageResId,
                "cost" to item.price
            )
            userDocRef.collection("petShop").add(petShopItem)
                .addOnFailureListener { e -> Log.e("FirestoreError", "Failed to add pet item: ${e.message}", e) }
        }

        // Create gallery subcollection
        galleryItems.forEach { item ->
            val galleryData = hashMapOf(
                "title" to item.type,
                "babyImage" to item.babyImage,
                "teenImage" to item.teenImage,
                "adultImage" to item.adultImage
            )
            userDocRef.collection("gallery").add(galleryData)
                .addOnFailureListener { e -> Log.e("FirestoreError", "Failed to add gallery item: ${e.message}", e) }
        }
    }
}
