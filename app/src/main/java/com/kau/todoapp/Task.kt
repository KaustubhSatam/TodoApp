package com.kau.todoapp

data class Task(
    val id: Int,
    val title: String,
    var isCompleted: Boolean = false
)
