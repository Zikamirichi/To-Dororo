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

        // Update views
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
        if (pets.isNotEmpty() && currentPetIndex in pets.indices) {
            petImageView.setImageResource(pets[currentPetIndex].drawableResId)
        } else {
            petImageView.setImageResource(R.drawable.adopt_a_pet)
        }
    }

    private fun feedPet() {
        if (pets.isEmpty() || currentPetIndex < 0 || currentPetIndex >= pets.size) {
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

        // Increase hearts and handle evolution stages
        pet.heartsPet += 100

        // Handle evolution stages
        if (pet.heartsPet >= pet.maxHearts) {
            when (pet.maxHearts) {
                3000 -> { // Evolve from Baby to Teen
                    pet.maxHearts = 5000
                    pet.heartsPet = 0 // Reset hearts for the Teen stage
                    pet.drawableResId = when (pet.type) {
                        "Cat" -> R.drawable.cat_teen
                        "Dog" -> R.drawable.dog_teen
                        "Parrot" -> R.drawable.parrot_teen
                        else -> R.drawable.adopt_a_pet
                    }
                    pet.stage = "teen"
                }
                5000 -> { // Evolve from Teen to Adult
                    pet.heartsPet = 5000 // Reset hearts to max for Adult stage
                    pet.drawableResId = when (pet.type) {
                        "Cat" -> R.drawable.cat_adult
                        "Dog" -> R.drawable.dog_adult
                        "Parrot" -> R.drawable.parrot_adult
                        else -> R.drawable.adopt_a_pet
                    }
                    pet.stage = "adult"
                }
            }
        }

        // Deduct hearts from user
        userHeartCount -= 100
        heartCountTextView.text = userHeartCount.toString()

        // Update the displayed heart count
        updateHeartDisplay()

        // Update Firestore with new pet and user data
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            Log.d("FirestorePaths", "User ID: $userId")
            Log.d("FirestorePaths", "Pet ID: ${pet.id}")

            val userRef = db.collection("users").document(userId)
            val petRef = db.collection("users").document(userId).collection("pets").document(pet.id)
            val galleryRef = db.collection("users").document(userId).collection("gallery")

            // Query for the gallery document where title matches pet.type
            galleryRef.whereEqualTo("title", pet.type).limit(1).get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.documents.isNotEmpty()) {
                        val galleryDocRef = querySnapshot.documents[0].reference

                        db.runTransaction { transaction ->
                            val petDocument = transaction.get(petRef)
                            if (petDocument.exists()) {
                                val updatedPet = petDocument.toObject(PetData::class.java)?.apply {
                                    heartsPet = pet.heartsPet
                                    maxHearts = pet.maxHearts
                                    drawableResId = pet.drawableResId
                                    stage = pet.stage
                                }
                                if (updatedPet != null) {
                                    transaction.set(petRef, updatedPet.toMap())
                                }
                            }
                            transaction.update(userRef, "hearts", userHeartCount)

                            // Update the gallery document based on pet stage
                            transaction.update(
                                galleryDocRef,
                                when (pet.stage) {
                                    "baby" -> "babyImage"
                                    "teen" -> "teenImage"
                                    "adult" -> "adultImage"
                                    else -> throw IllegalArgumentException("Unknown stage: ${pet.stage}")
                                },
                                pet.drawableResId
                            )
                        }.addOnSuccessListener {
                            Log.d("VirtualPetFragment", "Pet, user data, and gallery updated successfully")
                            // Fetch the entire pet list to reflect any updates
                            fetchPetData(userId)
                        }.addOnFailureListener { exception ->
                            Log.e("VirtualPetFragment", "Error updating pet, user data, or gallery", exception)
                        }
                    } else {
                        Log.e("VirtualPetFragment", "No matching gallery document found")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("VirtualPetFragment", "Error querying gallery documents", exception)
                }
        }
    }

    private fun fetchPetData(userId: String) {
        db.collection("users").document(userId).collection("pets")
            .orderBy("order", com.google.firebase.firestore.Query.Direction.DESCENDING) // Sort by order
            .get()
            .addOnSuccessListener { result ->
                pets.clear()
                for (document in result) {
                    val pet = document.toObject(PetData::class.java)
                    if (pet != null) {
                        pets.add(pet)
                    }
                }
                if (pets.isNotEmpty() && currentPetIndex >= pets.size) {
                    currentPetIndex = pets.size - 1
                }
                updatePetImageView()
                updateHeartDisplay()
                updateButtonStates()
            }
            .addOnFailureListener { exception ->
                Log.e("VirtualPetFragment", "Error fetching pet data", exception)
            }
    }




    private fun fetchUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userHeartCount = document.getLong("hearts") ?: 0
                        heartCountTextView.text = userHeartCount.toString()
                        // Fetch pet data as well
                        fetchPetData(userId)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("VirtualPetFragment", "Error fetching user data", exception)
                }
        }
    }

    private fun updateHeartDisplay() {
        if (pets.isNotEmpty() && currentPetIndex in pets.indices) {
            val pet = pets[currentPetIndex]
            tvHeartsUserGallery.text = "${pet.heartsPet} / ${pet.maxHearts}"
        } else {
            tvHeartsUserGallery.text = "-- / --"
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
