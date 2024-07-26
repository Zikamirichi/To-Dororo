package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(private val todoItems: List<TodoItem>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = todoItems[position]

        // Set date header visibility based on whether it's a new date
        if (currentItem.isDateHeader) {
            holder.textDate.visibility = View.VISIBLE
            holder.separatorLine.visibility = View.VISIBLE
            holder.textDate.text = currentItem.date
        } else {
            // Remove views if not a date header
            (holder.itemView as? ViewGroup)?.apply {
                removeView(holder.textDate)
                removeView(holder.separatorLine)
            }
        }

        // Bind title
        holder.activityButton.text = currentItem.title
        holder.activityButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("taskTitle", currentItem.title)
                putExtra("date", currentItem.date)

            }
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return todoItems.size
    }

    // ViewHolder class
    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDate: TextView = itemView.findViewById(R.id.text_date)
        val activityButton: Button = itemView.findViewById(R.id.activity_button)
        val separatorLine: View = itemView.findViewById(R.id.separator_line)
    }
}

