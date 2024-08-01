package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
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
    private lateinit var heartCountTextView: TextView
    private lateinit var placeholderTextView: TextView
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_to_do_list, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_todo)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        heartCountTextView = view.findViewById(R.id.heart_count)
        placeholderTextView = view.findViewById(R.id.placeholder_text)

        fetchTodoItems()
        fetchHeartCount()

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

    override fun onResume() {
        super.onResume()
        fetchTodoItems() // Refresh the to-do items when the fragment is resumed
        fetchHeartCount() // Refresh the heart count when the fragment is resumed
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

                    // Check if the date is before today
                    val today = Calendar.getInstance().time
                    if (date != null && date.before(today)) {
                        firestore.collection("users")
                            .document(userId)
                            .collection("notes")
                            .document(document.id)
                            .update("isCompleted", true)
                    } else {
                        val todoItem = TodoItem(title, formattedDate, isCompleted = isCompleted)
                        dateMap.getOrPut(formattedDate) { mutableListOf() }.add(todoItem)
                    }
                }

                // Sort dates and create list
                val sortedDates = dateMap.keys.sorted()
                val sortedItems = mutableListOf<TodoItem>()

                for (date in sortedDates) {
                    sortedItems.add(TodoItem(date, date, true)) // Date Header
                    sortedItems.addAll(dateMap[date] ?: emptyList())
                }

                if (sortedItems.isEmpty()) {
                    placeholderTextView.visibility = View.VISIBLE
                } else {
                    placeholderTextView.visibility = View.GONE
                }

                adapter = TodoAdapter(sortedItems)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Handle failure
                exception.printStackTrace()
            }
    }

    private fun fetchHeartCount() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            // Handle case where user is not logged in
            return
        }

        val userId = currentUser.uid

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val heartCount = document.getLong("hearts") ?: 0
                heartCountTextView.text = heartCount.toString()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                exception.printStackTrace()
            }
    }
}
