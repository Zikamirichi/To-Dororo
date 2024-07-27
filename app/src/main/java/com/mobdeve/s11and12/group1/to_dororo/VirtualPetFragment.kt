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
    private val pets = mutableListOf<PetData>()
    private var currentPetIndex = 0
    private lateinit var db: FirebaseFirestore
    private lateinit var heartCountTextView: TextView
    private lateinit var tvHeartsUserGallery: TextView
    private lateinit var leftButton: ImageButton
    private lateinit var rightButton: ImageButton
    private lateinit var petImageView: ImageView
    private var userHeartCount: Long = 0

    private val petShopActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                // Reload user data to reflect updated heart count
                fetchUserData()
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

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Set button click listeners
        leftButton.setOnClickListener { switchPet(-1) }
        rightButton.setOnClickListener { switchPet(1) }
        feedButton.setOnClickListener { feedPet() }

        // Existing button setups
        setupOtherButtons(view)

        // Fetch data from Firestore
        fetchUserData()

        // update views
        updatePetImageView()
        updateButtonStates()
        updateHeartDisplay()

        return view
    }

    private fun switchPet(direction: Int) {
        if (pets.isNotEmpty()) {
            currentPetIndex = (currentPetIndex + direction + pets.size) % pets.size
            updatePetImageView()
            updateButtonStates()
            updateHeartDisplay()
        }
    }

    private fun updatePetImageView() {
        if (pets.isNotEmpty()) {
            petImageView.setImageResource(pets[currentPetIndex].drawableResId)
        } else {
            petImageView.setImageResource(R.drawable.adopt_a_pet)
        }
    }

    private fun feedPet() {
        if (pets.isEmpty() || currentPetIndex < 0) {
            Toast.makeText(requireContext(), "No pet to feed", Toast.LENGTH_SHORT).show()
            return
        }

        val pet = pets[currentPetIndex]

        // Prevent feeding if the pet is an adult
        if (pet.maxHearts == 5000 && pet.heartsPet >= 5000) {
            Toast.makeText(requireContext(), "This pet is already an adult and cannot be fed.", Toast.LENGTH_SHORT).show()
            return
        }

        // Prevent feeding if the user has no hearts left
        if (userHeartCount < 100) {
            Toast.makeText(requireContext(), "Not enough hearts to feed the pet!", Toast.LENGTH_SHORT).show()
            return
        }

        // Log current pet state before feeding
        Log.d("VirtualPetFragment", "Feeding pet: ${pet.id}, type: ${pet.type}, heartsPet: ${pet.heartsPet}, maxHearts: ${pet.maxHearts}, drawableResId: ${pet.drawableResId}")

        // Increase hearts and handle evolution stages
        pet.heartsPet += 100

        // Handle evolution stages
        when {
            pet.stage == "baby" && pet.heartsPet >= 3000 -> { // Evolve from Baby to Teen
                pet.stage = "teen"
                pet.maxHearts = 5000
                pet.heartsPet = 0 // Reset hearts for the Teen stage
                pet.drawableResId = when (pet.type) {
                    "Cat" -> R.drawable.cat_teen
                    "Dog" -> R.drawable.dog_teen
                    "Parrot" -> R.drawable.parrot_teen
                    else -> pet.drawableResId
                }
            }
            pet.stage == "teen" && pet.heartsPet >= 5000 -> { // Evolve from Teen to Adult
                pet.stage = "adult"
                pet.maxHearts = 5000
                pet.heartsPet = 5000 // Keep hearts at max for Adult stage
                pet.drawableResId = when (pet.type) {
                    "Cat" -> R.drawable.cat_adult
                    "Dog" -> R.drawable.dog_adult
                    "Parrot" -> R.drawable.parrot_adult
                    else -> pet.drawableResId
                }
            }
        }

        // Log updated pet state after feeding
        Log.d("VirtualPetFragment", "After feeding: ${pet.id}, type: ${pet.type}, heartsPet: ${pet.heartsPet}, maxHearts: ${pet.maxHearts}, drawableResId: ${pet.drawableResId}")

        // Deduct hearts from user
        userHeartCount -= 100
        heartCountTextView.text = userHeartCount.toString()

        // Update Firestore with new pet and user data
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val userRef = db.collection("users").document(userId)
            val petRef = db.collection("users").document(userId).collection("pets").document(pet.id) // Use unique pet ID

            db.runTransaction { transaction ->
                val petDocument = transaction.get(petRef)
                if (petDocument.exists()) {
                    // Update pet data
                    transaction.set(petRef, mapOf(
                        "type" to pet.type,
                        "heartsPet" to pet.heartsPet,
                        "stage" to pet.stage,
                        "maxHearts" to pet.maxHearts,
                        "drawableResId" to pet.drawableResId // Ensure this field is updated
                    ))
                }
                // Update user hearts
                transaction.update(userRef, "hearts", userHeartCount)
            }.addOnSuccessListener {
                Log.d("VirtualPetFragment", "Pet and user data updated successfully")
                // Refresh pet data to update UI
                fetchPetHeartCount(userId)
            }.addOnFailureListener { exception ->
                Log.e("VirtualPetFragment", "Error updating pet and user data", exception)
            }
        }

        // Update the ImageView with the new pet drawable
        updatePetImageView()
    }




    private fun updateHeartDisplay() {
        if (pets.isNotEmpty()) {
            val pet = pets[currentPetIndex]
            tvHeartsUserGallery.text = "${pet.heartsPet} / ${pet.maxHearts}"
        } else {
            tvHeartsUserGallery.text = "-- / --"
        }
    }

    private fun fetchUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val userRef = db.collection("users").document(userId)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val heartCount = document.getLong("hearts")
                        if (heartCount != null) {
                            userHeartCount = heartCount
                            heartCountTextView.text = userHeartCount.toString()
                            fetchPetHeartCount(userId)
                        } else {
                            Log.e("VirtualPetFragment", "Heart count is null")
                        }
                    } else {
                        Log.e("VirtualPetFragment", "User document does not exist")
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
                pets.clear()
                for (document in documents) {
                    val petId = document.id // Unique ID for each pet document
                    val petType = document.getString("type") ?: "unknown"
                    val heartsPet = document.getLong("heartsPet") ?: 0
                    val stage = document.getString("stage") ?: "baby"
                    val drawableResId = when (petType) {
                        "Cat" -> when (stage) {
                            "teen" -> R.drawable.cat_teen
                            "adult" -> R.drawable.cat_adult
                            else -> R.drawable.cat_baby
                        }
                        "Dog" -> when (stage) {
                            "teen" -> R.drawable.dog_teen
                            "adult" -> R.drawable.dog_adult
                            else -> R.drawable.dog_baby
                        }
                        "Parrot" -> when (stage) {
                            "teen" -> R.drawable.parrot_teen
                            "adult" -> R.drawable.parrot_adult
                            else -> R.drawable.parrot_baby
                        }
                        else -> R.drawable.adopt_a_pet
                    }
                    val maxHearts = when (stage) {
                        "teen" -> 5000
                        "adult" -> 5000
                        else -> 3000
                    }
                    pets.add(PetData(petId, petType, heartsPet.toInt(), drawableResId, maxHearts, stage))
                }
                if (pets.isNotEmpty()) {
                    currentPetIndex = 0
                    updatePetImageView()
                    updateHeartDisplay()
                    updateButtonStates()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("VirtualPetFragment", "Error fetching pet data", exception)
            }
    }


    private fun updateButtonStates() {
        leftButton.isEnabled = pets.size > 1
        rightButton.isEnabled = pets.size > 1
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
}

