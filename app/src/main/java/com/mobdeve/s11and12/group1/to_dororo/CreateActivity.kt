package com.mobdeve.s11and12.group1.to_dororo

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class CreateActivity : AppCompatActivity() {

    private lateinit var dateTextView: TextView
    private lateinit var setTimerTextView: TextView
    private lateinit var totalTextView: TextView
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        // Initialize views
        dateTextView = findViewById(R.id.date_text)
        setTimerTextView = findViewById(R.id.set_timer_text)
        totalTextView = findViewById(R.id.total_time_text)
        val helpButton = findViewById<ImageButton>(R.id.help_icon)
        val historyButton = findViewById<ImageButton>(R.id.history_icon)

        // Set initial date to today
        calendar = Calendar.getInstance()
        updateDateText(calendar.time)

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

        // Set Timer text click listener
//        setTimerTextView.setOnClickListener {
//            startActivity(Intent(this@CreateActivity, PomodoroActivity::class.java))
//        }

        // Set default total time
        updateTotalTimeText("45 m 34s") // Default value, replace with actual logic as needed

        // Underline "Set timer" text
        setTimerTextView.paintFlags = setTimerTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
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
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        dateTextView.text = "Date: $formattedDate"
    }

    private fun updateTotalTimeText(time: String) {
        totalTextView.text = "Total: $time"
    }
}
