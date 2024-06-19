package com.mobdeve.s11and12.group1.to_dororo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            TodoItem("Task 1", 3, "Today", true),
            TodoItem("Task 2", 5, "Today", false),
            TodoItem("Task 3", 2, "Tomorrow", true),
            TodoItem("Task 4", 1, "Tomorrow", false),
            TodoItem("Task 5", 4, "Specific Date", true),
            TodoItem("Task 6", 2, "Specific Date", false),
            TodoItem("Task 6", 2, "Specific Date", false),
            TodoItem("Task 6", 2, "Specific Date", false),
            TodoItem("Task 6", 2, "Specific Date", false)
            // Add more as needed
        )

        adapter = TodoAdapter(todoList)
        recyclerView.adapter = adapter

        return view
    }
}
