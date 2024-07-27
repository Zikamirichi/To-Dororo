package com.mobdeve.s11and12.group1.to_dororo

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var taskTitleTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var totalTimeTextView: TextView
    private lateinit var noteEditText: EditText
    private lateinit var saveButton: Button

    private var currentTaskTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        taskTitleTextView = findViewById(R.id.todo_title)
        dateTextView = findViewById(R.id.date_text)
        totalTimeTextView = findViewById(R.id.total_time_text)
        noteEditText = findViewById(R.id.note_edit_text)
        saveButton = findViewById(R.id.save_button)

        currentTaskTitle = intent.getStringExtra("taskTitle")

        if (currentTaskTitle != null) {
            fetchTaskDetails(currentTaskTitle!!)
        } else {
            Log.e("DetailActivity", "Task title is null")
        }

        dateTextView.setOnClickListener {
            showDatePicker()
        }

        saveButton.setOnClickListener {
            saveChanges()
        }
    }

    private fun fetchTaskDetails(taskTitle: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Log.e("DetailActivity", "User is not logged in.")
            return
        }

        val userId = currentUser.uid

        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .whereEqualTo("title", taskTitle)
            .get()
            .addOnSuccessListener { result ->
                if (result != null && !result.isEmpty) {
                    val document = result.documents[0]
                    val title = document.getString("title") ?: ""
                    val dateString = document.getString("date") ?: ""
                    val totalTime = document.getString("totalTime") ?: ""
                    val note = document.getString("body") ?: ""

                    val date = try {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
                    } catch (e: Exception) {
                        null
                    }
                    val formattedDate = date?.let { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(it) } ?: dateString

                    taskTitleTextView.text = title
                    dateTextView.text = "Date: $formattedDate"
                    totalTimeTextView.text = "Total: $totalTime"
                    noteEditText.setText(note)
                } else {
                    Log.e("DetailActivity", "No matching document found.")
                    taskTitleTextView.text = "N/A"
                    dateTextView.text = "Date: N/A"
                    totalTimeTextView.text = "Total: N/A"
                    noteEditText.setText("")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DetailActivity", "Error fetching document: ${exception.message}")
                taskTitleTextView.text = "N/A"
                dateTextView.text = "Date: N/A"
                totalTimeTextView.text = "Total: N/A"
                noteEditText.setText("")
            }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val newDate = Calendar.getInstance()
            newDate.set(year, month, dayOfMonth)
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(newDate.time)
            dateTextView.text = "Date: ${SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(newDate.time)}"
        }

        DatePickerDialog(
            this, dateSetListener,
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveChanges() {
        val title = taskTitleTextView.text.toString()
        val dateText = dateTextView.text.toString().removePrefix("Date: ").trim()
        val totalTime = totalTimeTextView.text.toString().removePrefix("Total: ").trim()
        val note = noteEditText.text.toString()

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Log.e("DetailActivity", "User is not logged in.")
            return
        }

        val userId = currentUser.uid

        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .whereEqualTo("title", currentTaskTitle)
            .get()
            .addOnSuccessListener { result ->
                if (result != null && !result.isEmpty) {
                    val document = result.documents[0]
                    document.reference.update(
                        mapOf(
                            "title" to title,
                            "date" to dateText,
                            "totalTime" to totalTime,
                            "body" to note
                        )
                    )
                        .addOnSuccessListener {
                            Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Log.e("DetailActivity", "Error saving changes: ${exception.message}")
                            Toast.makeText(this, "Error saving changes. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.e("DetailActivity", "No matching document found for update.")
                    Toast.makeText(this, "Error: Task not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DetailActivity", "Error fetching document for update: ${exception.message}")
                Toast.makeText(this, "Error fetching data. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }
}
