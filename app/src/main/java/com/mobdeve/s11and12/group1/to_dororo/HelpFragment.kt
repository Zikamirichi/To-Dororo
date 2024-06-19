package com.mobdeve.s11and12.group1.to_dororo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// imports
import android.view.animation.Animation
import android.view.animation.AnimationUtils


class HelpFragment : Fragment() {

    private lateinit var fragmentContainer: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_help, container, false)

        // Find the fragment container
        fragmentContainer = rootView.findViewById(R.id.help_layout)

        // Slide in animation
        val slideInAnimation: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
        fragmentContainer.startAnimation(slideInAnimation)

        // Make the fragment visible after the animation ends
        slideInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                fragmentContainer.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        return rootView
    }
}