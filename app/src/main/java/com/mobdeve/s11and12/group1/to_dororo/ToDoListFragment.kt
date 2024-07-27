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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ToDoListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TodoAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_to_do_list, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_todo)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchTodoItems()

        val helpView = view.findViewById<ImageButton>(R.id.help_icon)
        helpView.setOnClickListener {
            startActivity(Intent(requireContext(), HelpActivity::class.java))
        }

        val createView = view.findViewById<ImageButton>(R.id.create_button)
        createView.setOnClickListener {
            startActivity(Intent(requireContext(), CreateActivity::class.java))
        }

        val historyButton = view.findViewById<ImageButton>(R.id.history_icon)
        historyButton.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }

        return view
    }

    private fun fetchTodoItems() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            // Handle case where user is not logged in
            return
        }

        val userId = currentUser.uid

        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .whereEqualTo("isCompleted", false) // Filter for incomplete tasks
            .get()
            .addOnSuccessListener { result ->
                val dateMap = mutableMapOf<String, MutableList<TodoItem>>()

                for (document in result) {
                    val title = document.getString("title") ?: ""
                    val dateString = document.getString("date") ?: ""
                    val date = try {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
                    } catch (e: Exception) {
                        null
                    }
                    val formattedDate = date?.let { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(it) } ?: dateString
                    val isCompleted = document.getBoolean("isCompleted") ?: false
                    val todoItem = TodoItem(title, formattedDate, isCompleted = isCompleted)

                    dateMap.getOrPut(formattedDate) { mutableListOf() }.add(todoItem)
                }

                // Sort dates and create list
                val sortedDates = dateMap.keys.sorted()
                val sortedItems = mutableListOf<TodoItem>()

                for (date in sortedDates) {
                    sortedItems.add(TodoItem(date, date, true)) // Date Header
                    sortedItems.addAll(dateMap[date] ?: emptyList())
                }

                adapter = TodoAdapter(sortedItems)
                recyclerView.adapter = adapter
            }

    }
}
