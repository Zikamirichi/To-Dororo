// ToDoItem.kt
package com.mobdeve.s11and12.group1.to_dororo

data class TodoItem(
    val title: String,
    val date: String = "", // Date for grouping tasks
    val isDateHeader: Boolean = false,
    val isCompleted: Boolean = false
)
