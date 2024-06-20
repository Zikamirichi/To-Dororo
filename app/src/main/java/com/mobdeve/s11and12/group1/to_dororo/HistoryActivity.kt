package com.mobdeve.s11and12.group1.to_dororo

// HistoryActivity.kt
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: MutableList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recycler_view)
        val clearButton: Button = findViewById(R.id.clear_button)

        taskList = mutableListOf(
            Task("Completed Task 1"),
            Task("Completed Task 2"),
            Task("Completed Task 3")
        )

        taskAdapter = TaskAdapter(taskList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        clearButton.setOnClickListener {
            taskAdapter.clearTasks()
        }
    }
}

