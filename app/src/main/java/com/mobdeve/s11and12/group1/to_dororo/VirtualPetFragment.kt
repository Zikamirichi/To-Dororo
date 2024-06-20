package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.mobdeve.s11and12.group1.to_dororo.HelpActivity

class VirtualPetFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_virtual_pet, container, false)

        // Find the help_icon ImageButton
        val helpIcon = view.findViewById<ImageButton>(R.id.help_icon)

        // Set OnClickListener for help_icon
        helpIcon.setOnClickListener {
            // Start HelpActivity
            startActivity(Intent(requireContext(), HelpActivity::class.java))
        }

        return view
    }
}