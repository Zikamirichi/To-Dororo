package com.mobdeve.s11and12.group1.to_dororo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PetGalleryAdapter (private val context: Context, private val petList: List<PetGalleryData>) :
    RecyclerView.Adapter<PetGalleryAdapter.PetGalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetGalleryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_petgallery, parent, false)
        return PetGalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetGalleryViewHolder, position: Int) {
        val pet = petList[position]
        holder.tvPetType.text = pet.title
        holder.ivPetBaby.setImageResource(pet.babyImage)
        holder.ivPetTeen.setImageResource(pet.teenImage)
        holder.ivPetAdult.setImageResource(pet.adultImage)
    }

    override fun getItemCount(): Int {
        return petList.size
    }

    class PetGalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPetType: TextView = itemView.findViewById(R.id.tvPetType)
        val ivPetBaby: ImageView = itemView.findViewById(R.id.ivPetBaby)
        val ivPetTeen: ImageView = itemView.findViewById(R.id.ivPetTeen)
        val ivPetAdult: ImageView = itemView.findViewById(R.id.ivPetAdult)
    }
}