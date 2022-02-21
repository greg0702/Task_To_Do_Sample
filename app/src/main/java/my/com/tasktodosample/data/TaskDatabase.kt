package my.com.tasktodosample.data

import android.content.Context
import androidx.room.*
import my.com.tasktodosample.converter.StringConverter
import my.com.tasktodosample.model.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(StringConverter::class)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object{

        @Volatile
        private var dbInstance: TaskDatabase? = null

        fun getDb(context: Context): TaskDatabase{

            val currentInstance = dbInstance

            if (currentInstance != null){
                return currentInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, TaskDatabase::class.java,"task_database").build()
                dbInstance = instance
                return instance
            }

        }

    }

}