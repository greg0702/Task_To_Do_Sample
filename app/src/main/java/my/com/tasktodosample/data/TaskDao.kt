package my.com.tasktodosample.data

import androidx.lifecycle.LiveData
import androidx.room.*
import my.com.tasktodosample.model.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("SELECT * FROM task_table ORDER BY id ASC")
    fun getAllTasks(): LiveData<List<Task>>

}