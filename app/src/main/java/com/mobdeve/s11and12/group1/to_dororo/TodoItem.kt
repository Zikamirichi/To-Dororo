package com.mobdeve.s11and12.group1.to_dororo

data class TodoItem(
    val title: String,
    val heartCount: Int,
    val date: String = "", // Date for grouping tasks
    val isDateHeader: Boolean = false // Indicates if it's a date header
)
