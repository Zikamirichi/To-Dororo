package com.mobdeve.s11and12.group1.to_dororo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import android.widget.Toast

class PetShopActivity : AppCompatActivity() {

    private lateinit var recyclerViewPetShop: RecyclerView
    private lateinit var petShopAdapter: PetShopAdapter
    private val petList = mutableListOf<PetShopItem>()
    private lateinit var db: FirebaseFirestore
    private lateinit var user: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_shop)

        // Initialize Firestore and Auth
        db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance()

        // Setup RecyclerView
        recyclerViewPetShop = findViewById(R.id.recyclerViewPetShop)
        petShopAdapter = PetShopAdapter(petList, ::onPetBought)
        recyclerViewPetShop.adapter = petShopAdapter
        recyclerViewPetShop.layoutManager = GridLayoutManager(this, 2)

        // Handle edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fetch pets from Firebase
        fetchPetsFromFirebase()
    }

    private fun fetchPetsFromFirebase() {
        db.collection("shop").get()
            .addOnSuccessListener { documents ->
                petList.clear()
                for (document in documents) {
                    val pet = document.toObject(PetShopItem::class.java)
                    petList.add(pet)
                }
                petShopAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("PetShopActivity", "Error fetching pets", exception)
            }
    }

    private fun onPetBought(pet: PetShopItem) {
        val userId = user.currentUser?.uid ?: return
        val userRef = db.collection("users").document(userId)
        val petsCollectionRef = db.collection("users").document(userId).collection("pets")

        db.runTransaction { transaction ->
            // Fetch user document to check heart count
            val userDoc = transaction.get(userRef)
            if (userDoc.exists()) {
                val currentHeartCount = userDoc.getLong("hearts") ?: 0L

                // Check if user has enough hearts
                if (currentHeartCount >= 1000) {
                    // Deduct 1000 hearts
                    transaction.update(userRef, "hearts", currentHeartCount - 1000)

                    val newPetData = PetData(
                        type = pet.type,
                        heartsPet = 0,
                        drawableResId = getDrawableResIdForVirtualPet(pet.type),
                        maxHearts = 3000,
                        stage = "baby"
                    )
                    val newPetRef = petsCollectionRef.document() // Create a new document reference with auto-generated ID
                    transaction.set(newPetRef, newPetData.toMap())
                    Toast.makeText(this, "Congratulations on your new pet!", Toast.LENGTH_SHORT).show()
                } else {
                    throw Exception("Not enough hearts to buy the pet")
                    Toast.makeText(this, "Not enough hearts to adopt the pet", Toast.LENGTH_SHORT).show()

                }
            } else {
                throw Exception("User document does not exist")
            }
        }.addOnSuccessListener {
            val resultIntent = Intent().apply {
                putExtra("newPetResId", getDrawableResIdForVirtualPet(pet.type))
                putExtra("petType", pet.type)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }.addOnFailureListener { exception ->
            Log.e("PetShopActivity", "Error processing pet purchase", exception)
        }
    }
    private fun getDrawableResIdForVirtualPet(petType: String): Int {
        return when (petType) {
            "Cat" -> R.drawable.cat_baby
            "Dog" -> R.drawable.dog_baby
            "Parrot" -> R.drawable.parrot_baby
            else -> R.drawable.adopt_a_pet
        }
    }
}
