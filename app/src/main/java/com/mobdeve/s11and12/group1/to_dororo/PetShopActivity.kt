package com.mobdeve.s11and12.group1.to_dororo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
        petShopAdapter = PetShopAdapter(petList)
        recyclerViewPetShop.adapter = petShopAdapter
        recyclerViewPetShop.layoutManager = GridLayoutManager(this, 2)

        // Handle edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}