// TodoAdapter.kt
package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(private val todoItems: List<TodoItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            TodoViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = todoItems[position]
        if (holder is DateHeaderViewHolder) {
            holder.bind(item.date)
        } else if (holder is TodoViewHolder) {
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int = todoItems.size

    override fun getItemViewType(position: Int): Int {
        return if (todoItems[position].isDateHeader) TYPE_DATE_HEADER else TYPE_TASK_ITEM
    }

    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDate: TextView = itemView.findViewById(R.id.text_date)

        fun bind(date: String) {
            textDate.text = date
        }
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val activityButton: Button = itemView.findViewById(R.id.activity_button)

        fun bind(todoItem: TodoItem) {
            activityButton.text = todoItem.title
            activityButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("taskTitle", todoItem.title)
                    putExtra("date", todoItem.date)
                }
                context.startActivity(intent)
            }
        }
    }
}

