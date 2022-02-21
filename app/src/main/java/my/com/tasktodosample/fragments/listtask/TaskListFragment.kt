package my.com.tasktodosample.fragments.listtask

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import my.com.tasktodosample.MainActivity
import my.com.tasktodosample.R
import my.com.tasktodosample.adapter.TaskListAdapter
import my.com.tasktodosample.databinding.FragmentTaskListBinding
import my.com.tasktodosample.viewmodel.TaskViewModel

class TaskListFragment : Fragment() {

    private val TAG = "TASK_APP_TAG"

    private lateinit var binding: FragmentTaskListBinding
    private lateinit var taskViewModel: TaskViewModel

    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val main = activity as MainActivity

        main.title = getString(R.string.taskListLabel)

        binding = FragmentTaskListBinding.inflate(inflater, container, false)

        val adapter = TaskListAdapter{ holder, task ->

            holder.root.setOnClickListener { nav.navigate(R.id.action_taskListFragment_to_addEditTaskFragment,
                bundleOf("taskId" to task.id, "taskTitle" to task.taskTitle, "taskBody" to task.taskBody,
                "taskImagePath" to task.taskImage, "taskCompleted" to task.taskCompleted, "isEdit" to 1)) }

        }
        binding.rvTaskList.adapter = adapter
        binding.rvTaskList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        taskViewModel.getAllTasks().observe(viewLifecycleOwner) { task ->
            adapter.setTaskList(task)

            Log.d(TAG, adapter.itemCount.toString())

            if (adapter.itemCount == 0){ setHasOptionsMenu(false) }
            else{ setHasOptionsMenu(true) }
        }

        binding.addFab.setOnClickListener { nav.navigate(R.id.action_taskListFragment_to_addEditTaskFragment, bundleOf("isEdit" to 0)) }

        return binding.root

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) { inflater.inflate(R.menu.delete_menu, menu) }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete){ confirmDeleteAllTasks() }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmDeleteAllTasks() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete All Tasks?")
        builder.setMessage("Are you sure to delete all tasks?")
        builder.setPositiveButton("Yes"){ _, _ -> deleteAllTasks() }
        builder.setNegativeButton("No"){ _, _ ->  }
        builder.show()

    }

    private fun deleteAllTasks() {
        taskViewModel.deleteAllTasks()
        Log.d(TAG, "All tasks deleted!")
        Toast.makeText(requireContext(), "All tasks are deleted successfully!", Toast.LENGTH_SHORT).show()
    }

}