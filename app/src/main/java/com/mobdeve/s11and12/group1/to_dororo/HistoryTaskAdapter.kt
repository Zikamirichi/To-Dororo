package com.mobdeve.s11and12.group1.to_dororo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryTaskAdapter(private val context: Context, private val taskList: MutableList<HistoryTask>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_TASK_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_DATE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.date_header, parent, false)
            DateHeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
            TaskViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = taskList[position]
        if (holder is DateHeaderViewHolder) {
            holder.bind(task.date)
        } else if (holder is TaskViewHolder) {
            holder.bind(task)
        }
    }

    override fun getItemCount(): Int = taskList.size

    override fun getItemViewType(position: Int): Int {
        return if (taskList[position].isDateHeader) TYPE_DATE_HEADER else TYPE_TASK_ITEM
    }

    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDate: TextView = itemView.findViewById(R.id.text_date)

        fun bind(date: String) {
            textDate.text = date
        }
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val activityButton: Button = itemView.findViewById(R.id.activity_button)

        fun bind(historyTask: HistoryTask) {
            activityButton.text = historyTask.title
            activityButton.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                    putExtra("taskTitle", historyTask.title)
                    putExtra("date", historyTask.date)
                    putExtra("viewOnly", true) // Set to true for view-only mode
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    fun clearTasks() {
        taskList.clear()
        notifyDataSetChanged()
    }
}
