package my.com.tasktodosample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import my.com.tasktodosample.R
import my.com.tasktodosample.fragments.listtask.TaskListFragmentDirections
import my.com.tasktodosample.model.Task

class TaskListAdapter: RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    private var taskList = emptyList<Task>()

    class ViewHolder(view: View, val context: Context): RecyclerView.ViewHolder(view){

        val root = view
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val itemLayout: LinearLayout = view.findViewById(R.id.task_item_layout)
        val txtTaskCompleted: TextView = view.findViewById(R.id.txtTaskCompleted)

    }

    fun setTaskList(task: List<Task>){

        this.taskList = task
        this.taskList.forEach {
            notifyItemInserted(this.taskList.indexOf(it))
            notifyItemChanged(this.taskList.indexOf(it))
            notifyItemRemoved(this.taskList.indexOf(it))
        }

        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return ViewHolder(view,parent.context)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentTask = taskList[position]

        holder.txtTitle.text = currentTask.taskTitle

        if (!currentTask.taskCompleted){
            holder.txtTaskCompleted.text = "Task Incomplete"
        }else{
            holder.txtTaskCompleted.text = "Task Completed"
        }

        if (position % 2 == 1){
            holder.itemLayout.setBackgroundColor(ContextCompat.getColor(holder.context, R.color.pastel_blue))
        }else{
            holder.itemLayout.setBackgroundColor(ContextCompat.getColor(holder.context, R.color.traffic_purple))
        }

        holder.root.setOnClickListener {

            val action = TaskListFragmentDirections.actionTaskListFragmentToUpdateTaskFragment(currentTask)
            holder.root.findNavController().navigate(action)

        }

    }

    override fun getItemCount(): Int { return this.taskList.size }



}