package com.mobdeve.s11and12.group1.to_dororo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyTaskAdapter: HistoryTaskAdapter
    private val taskList: MutableList<HistoryTask> = mutableListOf()
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recycler_view)
        val clearButton: Button = findViewById(R.id.clear_button)

        recyclerView.layoutManager = LinearLayoutManager(this)
        historyTaskAdapter = HistoryTaskAdapter(this, taskList)
        recyclerView.adapter = historyTaskAdapter

        clearButton.setOnClickListener {
            historyTaskAdapter.clearTasks()
        }

        fetchCompletedTasks()
    }

    private fun fetchCompletedTasks() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            // Handle case where user is not logged in
            return
        }

        val userId = currentUser.uid

        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .whereEqualTo("isCompleted", true) // Filter for completed tasks
            .get()
            .addOnSuccessListener { result ->
                val dateMap = mutableMapOf<String, MutableList<HistoryTask>>()

                for (document in result) {
                    val title = document.getString("title") ?: ""
                    val dateString = document.getString("date") ?: ""
                    val date = try {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
                    } catch (e: Exception) {
                        null
                    }
                    val formattedDate = date?.let { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(it) } ?: dateString
                    val historyTask = HistoryTask(title, formattedDate, isDateHeader = false)

                    dateMap.getOrPut(formattedDate) { mutableListOf() }.add(historyTask)
                }

                // Sort dates and create list
                val sortedDates = dateMap.keys.sorted()
                val sortedTasks = mutableListOf<HistoryTask>()

                for (date in sortedDates) {
                    sortedTasks.add(HistoryTask(date, date, isDateHeader = true)) // Date Header
                    sortedTasks.addAll(dateMap[date] ?: emptyList())
                }

                taskList.clear()
                taskList.addAll(sortedTasks)
                historyTaskAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
            }
    }
}
