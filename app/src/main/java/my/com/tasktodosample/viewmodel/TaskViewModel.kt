package my.com.tasktodosample.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.com.tasktodosample.data.TaskDatabase
import my.com.tasktodosample.model.Task
import my.com.tasktodosample.repository.TaskRepository

class TaskViewModel(application: Application): ViewModel(){

    private val tasksList: LiveData<List<Task>>
    private val taskRepo: TaskRepository

    init {

        val taskDao = TaskDatabase.getDb(application).taskDao()
        taskRepo = TaskRepository(taskDao)
        tasksList = taskRepo.getTaskList

    }

    fun addTask(task: Task){
        viewModelScope.launch { taskRepo.addTask(task) }
    }

    fun updateTask(task: Task){
        viewModelScope.launch { taskRepo.updateTask(task) }
    }

    fun getAllTasks(): LiveData<List<Task>>{ return tasksList }

}