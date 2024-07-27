package com.mobdeve.s11and12.group1.to_dororo

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

class PetShopActivity : AppCompatActivity() {

    private lateinit var recyclerViewPetShop: RecyclerView
    private lateinit var petShopAdapter: PetShopAdapter
    private val petList = mutableListOf<PetShopItem>()
    private lateinit var db: FirebaseFirestore
    private lateinit var user: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
        val resultIntent = Intent()
        resultIntent.putExtra("newPetResId", pet.imageResId)  // Changed from drawableResId to imageResId
        resultIntent.putExtra("petType", pet.type)
        setResult(RESULT_OK, resultIntent)

        // Add pet to user's collection in Firestore
        val userId = user.currentUser?.uid ?: return
        val petData = hashMapOf(
            "type" to pet.type,
            "imageResId" to pet.imageResId,
            "price" to pet.price
        )
        db.collection("users").document(userId).collection("pets").add(petData)
            .addOnSuccessListener {
                finish() // Close the PetShopActivity
            }
            .addOnFailureListener { exception ->
                Log.e("PetShopActivity", "Error adding pet to user collection", exception)
            }
    }
}
