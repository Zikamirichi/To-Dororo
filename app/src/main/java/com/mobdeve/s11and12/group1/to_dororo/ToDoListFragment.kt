package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ToDoListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_to_do_list, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_todo)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Dummy data for demonstration
        val todoList = mutableListOf(
            TodoItem("Task 1", "Today", true), // Date header for "Today"
            TodoItem("Task 2", "Today"),
            TodoItem("Task 3", "Tomorrow", true), // Date header for "Tomorrow"
            TodoItem("Task 4", "Tomorrow"),
            TodoItem("Task 5", "Specific Date", true), // Date header for "Specific Date"
            TodoItem("Task 6", "Specific Date"),
            TodoItem("Task 7", "Specific Date"),
            TodoItem("Task 8", "Specific Date")
            // Add more as needed
        )

        adapter = TodoAdapter(todoList)
        recyclerView.adapter = adapter

        // For Help button
        val helpView = view.findViewById<ImageButton>(R.id.help_icon)
        helpView.setOnClickListener {
            startActivity(Intent(requireContext(), HelpActivity::class.java))
        }

        // For Create button
        val createView = view.findViewById<ImageButton>(R.id.create_button)
        createView.setOnClickListener {
            startActivity(Intent(requireContext(), CreateActivity::class.java))
        }

        // For History Button
        val historyButton = view.findViewById<ImageButton>(R.id.history_icon)
        historyButton.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }

        return view
    }
}
