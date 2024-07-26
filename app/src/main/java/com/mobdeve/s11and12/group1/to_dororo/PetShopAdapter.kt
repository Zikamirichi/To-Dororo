package com.mobdeve.s11and12.group1.to_dororo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PetShopAdapter(
    private val petList: List<PetShopData>,
    private val onPetBought: (PetShopData) -> Unit
) : RecyclerView.Adapter<PetShopAdapter.PetViewHolder>() {

    inner class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPetType: TextView = itemView.findViewById(R.id.tvPetType)
        val ibBuyPet: ImageButton = itemView.findViewById(R.id.ibBuyPet)
        val ivHeartPet: ImageView = itemView.findViewById(R.id.ivHeartPet)
        val tvPetPrice: TextView = itemView.findViewById(R.id.tvPetPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_petshop, parent, false)
        return PetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = petList[position]
        holder.tvPetType.text = pet.type
        holder.ibBuyPet.setImageResource(pet.imageResId)
        holder.ivHeartPet.setImageResource(R.drawable.heart)

        if (pet.type == "Coming Soon") {
            holder.ivHeartPet.visibility = View.GONE
            holder.tvPetPrice.visibility = View.GONE
            holder.ibBuyPet.isEnabled = false
            holder.ibBuyPet.setOnClickListener(null)
        } else {
            holder.ivHeartPet.visibility = View.VISIBLE
            holder.tvPetPrice.visibility = View.VISIBLE
            holder.tvPetPrice.text = "100"
            holder.ibBuyPet.isEnabled = true
            holder.ibBuyPet.setOnClickListener {
                onPetBought(pet)
            }
        }
    }

    override fun getItemCount(): Int = petList.size
}
