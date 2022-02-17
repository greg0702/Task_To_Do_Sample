package my.com.tasktodosample.repository

import androidx.lifecycle.LiveData
import my.com.tasktodosample.data.TaskDao
import my.com.tasktodosample.model.Task

class TaskRepository(taskDao: TaskDao) {

    private val taskDaoIns = taskDao

    val getTaskList: LiveData<List<Task>> = taskDaoIns.getAllTasks()

    suspend fun addTask(task: Task){ taskDaoIns.addTask(task) }

    suspend fun updateTask(task: Task){ taskDaoIns.updateTask(task) }

    suspend fun deleteTask(task: Task){ taskDaoIns.deleteTask(task) }

    suspend fun deleteAllTask(){ taskDaoIns.deleteAllTask() }

}