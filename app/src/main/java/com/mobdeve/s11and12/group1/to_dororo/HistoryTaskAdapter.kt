package com.mobdeve.s11and12.group1.to_dororo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class HistoryTaskAdapter(private val context: Context, private val taskList: MutableList<HistoryTask>) : RecyclerView.Adapter<HistoryTaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.activityButton.text = task.title
        // Add click listeners for buttons if needed
        holder.activityButton.setOnClickListener {
            // Launch DetailActivity with task details
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("taskTitle", task.title)
                // Add more extras as needed (date, total time, note)
        }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun clearTasks() {
        taskList.clear()
        notifyDataSetChanged()
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityButton: Button = itemView.findViewById(R.id.activity_button)
        val designActivityButton: Button = itemView.findViewById(R.id.design_activity_button)
    }
}

