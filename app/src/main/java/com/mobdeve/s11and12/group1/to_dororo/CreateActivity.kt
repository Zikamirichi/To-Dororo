package com.mobdeve.s11and12.group1.to_dororo

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CreateActivity : AppCompatActivity() {

    private lateinit var dateTextView: TextView
    private lateinit var setTimerTextView: TextView
    private lateinit var totalTextView: TextView
    private lateinit var titleEditText: EditText
    private lateinit var noteEditText: EditText
    private lateinit var heartCountTextView: TextView
    private lateinit var calendar: Calendar
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        dateTextView = findViewById(R.id.date_text)
        setTimerTextView = findViewById(R.id.set_timer_text)
        totalTextView = findViewById(R.id.total_time_text)
        titleEditText = findViewById(R.id.todo_title)
        noteEditText = findViewById(R.id.note_edit_text)
        heartCountTextView = findViewById(R.id.heart_count)
        val helpButton = findViewById<ImageButton>(R.id.help_icon)
        val historyButton = findViewById<ImageButton>(R.id.history_icon)
        val saveButton = findViewById<Button>(R.id.save_button)

        // Set initial date to today
        calendar = Calendar.getInstance()
        updateDateText(calendar.time)

        // Fetch and display the number of hearts
        fetchHeartCount()

        // For History Button
        historyButton.setOnClickListener {
            startActivity(Intent(this@CreateActivity, HistoryActivity::class.java))
        }

        // Help button click listener
        helpButton.setOnClickListener {
            startActivity(Intent(this@CreateActivity, HelpActivity::class.java))
        }

        // Date text click listener
        dateTextView.setOnClickListener {
            showDatePicker()
        }

        // Set default total time
        updateTotalTimeText("Total: N/A") // Default value, replace with actual logic as needed

        // Set default title
        titleEditText.setText("Insert Title Here")

        // Underline "Set timer" text
        setTimerTextView.paintFlags = setTimerTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Save button click listener
        saveButton.setOnClickListener {
            saveNote()
        }
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                updateDateText(calendar.time)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun updateDateText(date: Date) {
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) // Updated format to exclude day
        val formattedDate = dateFormat.format(date)
        dateTextView.text = "Date: $formattedDate"
    }

    private fun updateTotalTimeText(time: String) {
        totalTextView.text = time
    }

    private fun fetchHeartCount() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userUid = currentUser.uid
            db.collection("users").document(userUid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val hearts = document.getLong("hearts") ?: 0
                        heartCountTextView.text = "$hearts"
                    } else {
                        heartCountTextView.text = "0"
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    heartCountTextView.text = "0"
                }
        } else {
            heartCountTextView.text = "0"
        }
    }

    private fun saveNote() {
        val title = titleEditText.text.toString()
        val body = noteEditText.text.toString()
        val date = dateTextView.text.toString().replace("Date: ", "")
        val totalTime = totalTextView.text.toString().replace("Total: ", "")
        val isCompleted = false

        val note = hashMapOf(
            "title" to title,
            "body" to body,
            "date" to date,
            "totalTime" to totalTime,
            "isCompleted" to isCompleted
        )

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userUid = currentUser.uid
            db.collection("users").document(userUid).collection("notes")
                .add(note)
                .addOnSuccessListener {
                    val intent = Intent(this@CreateActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
