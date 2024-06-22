package com.mobdeve.s11and12.group1.to_dororo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Retrieve task details from intent extras
        val taskTitle = intent.getStringExtra("taskTitle")
        val date = intent.getStringExtra("date")
        val totalTime = intent.getStringExtra("totalTime")
        val note = intent.getStringExtra("note")

        // Initialize views using findViewById
        val taskTitleTextView: TextView = findViewById(R.id.todo_title)
        val dateTextView: TextView = findViewById(R.id.date_text)
        val totalTimeTextView: TextView = findViewById(R.id.total_time_text)
        val noteTextView: TextView = findViewById(R.id.note_text)

        // Set text to views
        taskTitleTextView.text = taskTitle
        dateTextView.text = "Date: $date"
        totalTimeTextView.text = "Total: $totalTime"
        noteTextView.text = note
    }
}
