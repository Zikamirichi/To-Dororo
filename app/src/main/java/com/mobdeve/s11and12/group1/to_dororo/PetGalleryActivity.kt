package com.mobdeve.s11and12.group1.to_dororo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class PetGalleryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var petGalleryAdapter: PetGalleryAdapter
    private lateinit var petGalleryList: MutableList<PetGalleryItem>

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

        petGalleryList = ArrayList()
        petGalleryList.add(PetGalleryItem("Cat", R.drawable.cat_baby, R.drawable.cat_teen, R.drawable.cat_adult))
        petGalleryList.add(PetGalleryItem("Dog", R.drawable.dog_baby, R.drawable.dog_teen, R.drawable.dog_adult))
        petGalleryList.add(PetGalleryItem("Parrot", R.drawable.parrot_baby, R.drawable.locked_petgallery, R.drawable.locked_petgallery))
        // Add more items if needed

        petGalleryAdapter = PetGalleryAdapter(this, petGalleryList)
        recyclerView.adapter = petGalleryAdapter
    }
}
