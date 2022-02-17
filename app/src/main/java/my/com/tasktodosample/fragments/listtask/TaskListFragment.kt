package my.com.tasktodosample.fragments.listtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import my.com.tasktodosample.R
import my.com.tasktodosample.adapter.TaskListAdapter
import my.com.tasktodosample.databinding.FragmentTaskListBinding
import my.com.tasktodosample.viewmodel.TaskViewModel

class TaskListFragment : Fragment() {

    private lateinit var binding: FragmentTaskListBinding
    private lateinit var taskViewModel: TaskViewModel

    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentTaskListBinding.inflate(inflater, container, false)

        val adapter = TaskListAdapter()
        binding.rvTaskList.adapter = adapter
        binding.rvTaskList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        taskViewModel.getAllTasks().observe(viewLifecycleOwner) { task -> adapter.setTaskList(task) }

        binding.addFab.setOnClickListener { nav.navigate(R.id.action_taskListFragment_to_addTaskFragment) }

        return binding.root

    }

}