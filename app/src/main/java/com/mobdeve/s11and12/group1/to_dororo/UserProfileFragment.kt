package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class UserProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        val editProfile: TextView = view.findViewById(R.id.edit_profile)
        val changePassword: TextView = view.findViewById(R.id.change_password)
        val logoutButton: Button = view.findViewById(R.id.logout_button)

        editProfile.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        changePassword.setOnClickListener {
            val intent = Intent(activity, ChangePassActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            val intent = Intent(activity, LogInActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
