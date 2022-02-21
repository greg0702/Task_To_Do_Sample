package my.com.tasktodosample.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "title")
    val taskTitle: String,

    @ColumnInfo(name = "body")
    val taskBody: String,

    @ColumnInfo(name = "image_path")
    val taskImage: List<String>,

    @ColumnInfo(name = "completed")
    val taskCompleted: Boolean

)