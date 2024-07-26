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
    private val pets = mutableListOf(R.drawable.adopt_a_pet) // Initially only adopt_a_pet
    private var currentPetIndex = 0
    private lateinit var db: FirebaseFirestore
    private lateinit var heartCountTextView: TextView
    private lateinit var tvHeartsUserGallery: TextView
    private lateinit var leftButton: ImageButton
    private lateinit var rightButton: ImageButton
    private lateinit var petImageView: ImageView

    private val petShopActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val newPetResId = result.data?.getIntExtra("newPetResId", R.drawable.adopt_a_pet)
                val petType = result.data?.getStringExtra("petType")
                if (newPetResId != null && newPetResId != R.drawable.adopt_a_pet) {
                    val drawableResId = when (petType) {
                        "Cat" -> R.drawable.cat_baby
                        "Dog" -> R.drawable.dog_baby
                        "Parrot" -> R.drawable.parrot_baby
                        else -> newPetResId
                    }

                    pets.add(drawableResId)
                    if (pets.size == 2 && pets[0] == R.drawable.adopt_a_pet) {
                        pets.removeAt(0) // Remove the adopt_a_pet drawable if another pet is added
                    }
                    currentPetIndex = pets.size - 1
                    petImageView.setImageResource(pets[currentPetIndex])
                    updateButtonStates()
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
        petImageView.setImageResource(pets[currentPetIndex])
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
        petImageView.setImageResource(pets[currentPetIndex])
        updateButtonStates()
    }

    private fun feedPet() {
        // Logic to feed the pet
        Toast.makeText(requireContext(), "Feeding pet ${currentPetIndex + 1}", Toast.LENGTH_SHORT).show()
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
        val petsRef = db.collection("users").document(userId).collection("pets")

        petsRef.get()
            .addOnSuccessListener { querySnapshot ->
                var totalHearts = 0
                for (document in querySnapshot.documents) {
                    val heartCount = document.getLong("heart_count") ?: 0
                    totalHearts += heartCount.toInt()
                }
                tvHeartsUserGallery.text = "$totalHearts / 1000" // Adjust total hearts / max hearts as needed
            }
            .addOnFailureListener { exception ->
                Log.e("VirtualPetFragment", "Error fetching pet heart counts", exception)
            }
    }

    private fun updateButtonStates() {
        leftButton.isClickable = pets.size > 1
        rightButton.isClickable = pets.size > 1
    }

    companion object {
        private const val PET_SHOP_REQUEST_CODE = 1001
    }
}
