package my.com.tasktodosample.fragments.listtask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.com.tasktodosample.R
import my.com.tasktodosample.databinding.FragmentTaskListBinding

class TaskListFragment : Fragment() {

    private lateinit var binding: FragmentTaskListBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentTaskListBinding.inflate(inflater, container, false)

        binding.addFab.setOnClickListener {
            nav.navigate(R.id.action_taskListFragment_to_addTaskFragment)
        }

        return binding.root

    }

}