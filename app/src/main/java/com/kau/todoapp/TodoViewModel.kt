package com.kau.todoapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow


class TodoViewModel : ViewModel() {
    private var nextId = 0
    private val _text = MutableStateFlow("")
    val text = _text

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks

    fun onTextChange(newText: String) {
        text.value = newText
    }

    fun addTask() {
        val newTask = Task(
            id = nextId++,
            title = _text.value
        )
        _tasks.value += newTask
        _text.value = ""
    }

    fun deleteTask(id: Int) {
        _tasks.value = tasks.value.filter { it.id != id }
    }

    fun clearAllTask() {
        _tasks.value = emptyList()
    }
}