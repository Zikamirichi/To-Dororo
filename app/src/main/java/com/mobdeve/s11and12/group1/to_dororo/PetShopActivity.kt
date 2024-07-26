package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PetShopActivity : AppCompatActivity() {

    private lateinit var recyclerViewPetShop: RecyclerView
    private lateinit var petShopAdapter: PetShopAdapter

    // Sample data for the RecyclerView
    private val petList = listOf(
        PetShopData("Cat", R.drawable.cat_petshop, 100),
        PetShopData("Dog", R.drawable.dog_petshop, 100),
        PetShopData("Parrot", R.drawable.parrot_petshop, 100),
        PetShopData("Coming Soon", R.drawable.comingsoon_petshop, 100)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pet_shop)

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
    }

    private fun onPetBought(pet: PetShopData) {
        val resultIntent = Intent()
        resultIntent.putExtra("newPetResId", pet.imageResId)
        resultIntent.putExtra("petType", pet.type)
        setResult(RESULT_OK, resultIntent)
        finish() // Close the PetShopActivity
    }
}
