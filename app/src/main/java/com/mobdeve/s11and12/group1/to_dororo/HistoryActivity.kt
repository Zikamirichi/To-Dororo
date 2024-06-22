package com.mobdeve.s11and12.group1.to_dororo

// HistoryActivity.kt
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyTaskAdapter: HistoryTaskAdapter
    private lateinit var taskList: MutableList<HistoryTask>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recycler_view)
        val clearButton: Button = findViewById(R.id.clear_button)

        taskList = mutableListOf(
            HistoryTask("Completed Task 1", "Today", true),
            HistoryTask("Completed Task 2", "Tomorrow", true),
            HistoryTask("Completed Task 3", "Specific Date", true),
        )

        historyTaskAdapter = HistoryTaskAdapter(this, taskList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = historyTaskAdapter

        clearButton.setOnClickListener {
            historyTaskAdapter.clearTasks()
        }
    }
}

