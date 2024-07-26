package com.mobdeve.s11and12.group1.to_dororo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth

class VirtualPetFragment : Fragment() {
    private data class Pet(val type: String, var hearts: Int, var drawableResId: Int)

    private val pets = mutableListOf(Pet("adopt_a_pet", 0, R.drawable.adopt_a_pet))
    private var currentPetIndex = 0
    private lateinit var db: FirebaseFirestore
    private lateinit var heartCountTextView: TextView
    private lateinit var tvHeartsUserGallery: TextView
    private lateinit var leftButton: ImageButton
    private lateinit var rightButton: ImageButton
    private lateinit var petImageView: ImageView
    private var userHeartCount = 0L // Track user's total heart count

    private val petShopActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val newPetResId = result.data?.getIntExtra("newPetResId", R.drawable.adopt_a_pet)
                val petType = result.data?.getStringExtra("petType")
                if (newPetResId != null && newPetResId != R.drawable.adopt_a_pet && petType != null) {
                    val drawableResId = when (petType) {
                        "Cat" -> R.drawable.cat_baby
                        "Dog" -> R.drawable.dog_baby
                        "Parrot" -> R.drawable.parrot_baby
                        else -> newPetResId
                    }

                    // Deduct 1000 hearts for unlocking the pet
                    if (userHeartCount >= 1000) {
                        userHeartCount -= 1000
                        heartCountTextView.text = userHeartCount.toString()
                        pets.add(Pet(petType, 0, drawableResId))
                        if (pets.size == 2 && pets[0].type == "adopt_a_pet") {
                            pets.removeAt(0) // Remove the adopt_a_pet if another pet is added
                        }
                        currentPetIndex = pets.size - 1
                        petImageView.setImageResource(pets[currentPetIndex].drawableResId)
                        updateButtonStates()
                        updateHeartCountInFirestore(userHeartCount) // Update Firestore with the new heart count
                    } else {
                        Toast.makeText(requireContext(), "Not enough hearts to unlock pet!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_virtual_pet, container, false)

        // Initialize views
        petImageView = view.findViewById(R.id.ivPet01)
        leftButton = view.findViewById(R.id.ibLeft_button)
        rightButton = view.findViewById(R.id.ibRight_button)
        val feedButton = view.findViewById<ImageButton>(R.id.ibFeed_button)
        heartCountTextView = view.findViewById(R.id.heart_count)
        tvHeartsUserGallery = view.findViewById(R.id.tvHeartsUserGallery)

        // Set initial pet
        petImageView.setImageResource(pets[currentPetIndex].drawableResId)
        updateButtonStates()

        // Set button click listeners
        leftButton.setOnClickListener { switchPet(-1) }
        rightButton.setOnClickListener { switchPet(1) }
        feedButton.setOnClickListener { feedPet() }

        // Existing button setups
        setupOtherButtons(view)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Fetch data from Firestore
        fetchUserData()

        return view
    }

    private fun switchPet(direction: Int) {
        currentPetIndex = (currentPetIndex + direction + pets.size) % pets.size
        petImageView.setImageResource(pets[currentPetIndex].drawableResId)
        updateButtonStates()
    }

    private fun feedPet() {
        if (pets.isEmpty() || currentPetIndex < 0) {
            Toast.makeText(requireContext(), "No pet to feed", Toast.LENGTH_SHORT).show()
            return
        }

        // Increment pet's heart count
        val pet = pets[currentPetIndex]
        pet.hearts += 100

        // Update pet evolution status and image based on heart count
        pet.drawableResId = when {
            pet.hearts >= 3000 -> {
                when (pet.type) {
                    "Cat" -> R.drawable.cat_adult
                    "Dog" -> R.drawable.dog_adult
                    "Parrot" -> R.drawable.parrot_adult
                    else -> pet.drawableResId
                }
            }
            pet.hearts >= 1000 -> {
                when (pet.type) {
                    "Cat" -> R.drawable.cat_teen
                    "Dog" -> R.drawable.dog_teen
                    "Parrot" -> R.drawable.parrot_teen
                    else -> pet.drawableResId
                }
            }
            else -> {
                when (pet.type) {
                    "Cat" -> R.drawable.cat_baby
                    "Dog" -> R.drawable.dog_baby
                    "Parrot" -> R.drawable.parrot_baby
                    else -> pet.drawableResId
                }
            }
        }

        // Update displayed heart count and pet image
        tvHeartsUserGallery.text = "${pet.hearts} / ${if (pet.hearts >= 3000) 3000 else 1000}"
        petImageView.setImageResource(pet.drawableResId)

        // Deduct hearts from user
        if (userHeartCount >= 100) {
            userHeartCount -= 100
            heartCountTextView.text = userHeartCount.toString()
            updateHeartCountInFirestore(userHeartCount)
        } else {
            Toast.makeText(requireContext(), "Not enough hearts to feed the pet!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateHeartCountInFirestore(newHeartCount: Long) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val userRef = db.collection("users").document(userId)

            userRef.update("heart_count", newHeartCount)
                .addOnSuccessListener {
                    Log.d("VirtualPetFragment", "User heart count updated successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("VirtualPetFragment", "Error updating heart count", exception)
                }
        }
    }

    private fun setupOtherButtons(view: View) {
        val helpView = view.findViewById<ImageButton>(R.id.help_icon)
        helpView.setOnClickListener {
            startActivity(Intent(requireContext(), HelpActivity::class.java))
        }

        val storeView = view.findViewById<ImageButton>(R.id.ibPetShop)
        storeView.setOnClickListener {
            petShopActivityResultLauncher.launch(Intent(requireContext(), PetShopActivity::class.java))
        }

        val galleryView = view.findViewById<ImageButton>(R.id.ibGallery)
        galleryView.setOnClickListener {
            startActivity(Intent(requireContext(), PetGalleryActivity::class.java))
        }

        val historyButton = view.findViewById<ImageButton>(R.id.history_icon)
        historyButton.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }
    }

    private fun fetchUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val userRef = db.collection("users").document(userId)

            // Fetch heart count from user's document
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val heartCount = document.getLong("heart_count") ?: 0
                        userHeartCount = heartCount
                        heartCountTextView.text = heartCount.toString()
                        fetchPetHeartCount(userId)
                    } else {
                        heartCountTextView.text = "0"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("VirtualPetFragment", "Error fetching heart count", exception)
                }
        } else {
            Log.e("VirtualPetFragment", "No authenticated user found")
        }
    }

    private fun fetchPetHeartCount(userId: String) {
        val petRef = db.collection("users").document(userId).collection("pets")
        petRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val petType = document.getString("type") ?: "adopt_a_pet"
                    val hearts = document.getLong("hearts")?.toInt() ?: 0
                    val drawableResId = when (petType) {
                        "Cat" -> when {
                            hearts >= 3000 -> R.drawable.cat_adult
                            hearts >= 1000 -> R.drawable.cat_teen
                            else -> R.drawable.cat_baby
                        }
                        "Dog" -> when {
                            hearts >= 3000 -> R.drawable.dog_adult
                            hearts >= 1000 -> R.drawable.dog_teen
                            else -> R.drawable.dog_baby
                        }
                        "Parrot" -> when {
                            hearts >= 3000 -> R.drawable.parrot_adult
                            hearts >= 1000 -> R.drawable.parrot_teen
                            else -> R.drawable.parrot_baby
                        }
                        else -> R.drawable.adopt_a_pet
                    }
                    pets.add(Pet(petType, hearts, drawableResId))
                }

                if (pets.isNotEmpty()) {
                    currentPetIndex = 0
                    petImageView.setImageResource(pets[currentPetIndex].drawableResId)
                    tvHeartsUserGallery.text = "${pets[currentPetIndex].hearts} / ${if (pets[currentPetIndex].hearts >= 3000) 3000 else 1000}"
                }

                updateButtonStates()
            }
            .addOnFailureListener { exception ->
                Log.e("VirtualPetFragment", "Error fetching pet data", exception)
            }
    }

    private fun updateButtonStates() {
        leftButton.isEnabled = pets.size > 1
        rightButton.isEnabled = pets.size > 1
    }
}
