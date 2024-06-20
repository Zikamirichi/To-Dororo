package com.mobdeve.s11and12.group1.to_dororo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(private val todoItems: List<TodoItem>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = todoItems[position]
        val previousItem = if (position > 0) todoItems[position - 1] else null

        // Set date header visibility based on whether it's a new date
        if (previousItem == null || previousItem.date != currentItem.date) {
            holder.textDate.visibility = View.VISIBLE
            holder.separatorLine.visibility = View.VISIBLE
            holder.textDate.text = currentItem.date
        } else {
            holder.textDate.visibility = View.GONE
            holder.separatorLine.visibility = View.GONE
        }

        // Bind title
        holder.textTitle.text = currentItem.title
    }

    override fun getItemCount(): Int {
        return todoItems.size
    }

    // ViewHolder class
    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDate: TextView = itemView.findViewById(R.id.text_date)
        val textTitle: TextView = itemView.findViewById(R.id.text_title)
        val separatorLine: View = itemView.findViewById(R.id.separator_line)
    }
}
