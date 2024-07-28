package com.mobdeve.s11and12.group1.to_dororo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import android.widget.TextView

class PetGalleryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var petGalleryAdapter: PetGalleryAdapter
    private val petGalleryList = mutableListOf<PetGalleryItem>()
    private lateinit var db: FirebaseFirestore
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var tvHeartsUserGallery: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pet_gallery)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewPetGallery)
        recyclerView.layoutManager = LinearLayoutManager(this)

        petGalleryAdapter = PetGalleryAdapter(this, petGalleryList)
        recyclerView.adapter = petGalleryAdapter

        tvHeartsUserGallery = findViewById(R.id.tvHeartsUserGallery)

        db = FirebaseFirestore.getInstance()

        fetchPetGalleryData()
        fetchUserHearts()
    }

    private fun fetchPetGalleryData() {
        val userId = user?.uid ?: return
        val petRef = db.collection("users").document(userId).collection("gallery")

        petRef.get()
            .addOnSuccessListener { documents ->
                petGalleryList.clear()
                for (document in documents) {
                    val title = document.getString("title") ?: "Unknown"

                    // Fetch drawable resource IDs
                    val babyImage = document.getLong("babyImage")?.toInt() ?: R.drawable.adopt_a_pet
                    val teenImage = document.getLong("teenImage")?.toInt() ?: R.drawable.adopt_a_pet
                    val adultImage =
                        document.getLong("adultImage")?.toInt() ?: R.drawable.adopt_a_pet

                    // Add item to list
                    petGalleryList.add(PetGalleryItem(title, babyImage, teenImage, adultImage))
                }

                petGalleryList.sortBy { item ->
                    when (item.type) {
                        "Cat" -> 0
                        "Dog" -> 1
                        "Parrot" -> 2
                        else -> 3
                    }
                }
                petGalleryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("PetGalleryActivity", "Error fetching pet gallery data", exception)
            }
    }

    private fun fetchUserHearts() {
        val userId = user?.uid ?: return
        val userRef = db.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                val hearts = document?.getLong("hearts") ?: 0
                tvHeartsUserGallery.text = "$hearts"
            }
    }

}
