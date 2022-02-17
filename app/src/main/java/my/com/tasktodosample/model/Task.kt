package my.com.tasktodosample.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "task_table")
data class Task(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "title")
    val taskTitle: String,

    @ColumnInfo(name = "body")
    val taskBody: String,

    @ColumnInfo(name = "image_path")
    val taskImage: String

): Parcelable