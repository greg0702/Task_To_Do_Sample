package my.com.tasktodosample.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import my.com.tasktodosample.data.TaskDatabase
import my.com.tasktodosample.model.Task
import my.com.tasktodosample.repository.TaskRepository

class TaskViewModel(application: Application): AndroidViewModel(application){

    private val tasksList: LiveData<List<Task>>
    private val taskRepo: TaskRepository

    private val imageLiveData: MutableLiveData<List<String>> = MutableLiveData()
    private val currentImageList = ArrayList<String>()

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

    fun deleteTask(task: Task){
        viewModelScope.launch { taskRepo.deleteTask(task) }
    }

    fun deleteAllTasks(){
        viewModelScope.launch { taskRepo.deleteAllTask() }
    }

    fun getAllTasks(): LiveData<List<Task>>{ return tasksList }

    fun setImageList(path: String){
        currentImageList.add(path)
        imageLiveData.value = currentImageList
    }

    fun getImageList(): LiveData<List<String>>{ return imageLiveData }

    fun removeImage(path: String){
        currentImageList.remove(path)
        imageLiveData.value = currentImageList
    }

}