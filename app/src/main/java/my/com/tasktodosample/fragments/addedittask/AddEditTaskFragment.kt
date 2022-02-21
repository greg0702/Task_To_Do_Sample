package my.com.tasktodosample.fragments.addedittask

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import my.com.tasktodosample.MainActivity
import my.com.tasktodosample.R
import my.com.tasktodosample.adapter.TaskViewPagerAdapter
import my.com.tasktodosample.databinding.FragmentAddEditTaskBinding
import my.com.tasktodosample.model.Task
import my.com.tasktodosample.viewmodel.TaskViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddEditTaskFragment : Fragment() {

    private val TAG = "TASK_APP_TAG"
    private val REQUEST_IMAGE_CAPTURE: Int = 123
    private val REQUEST_SELECT_IMAGE: Int = 200
    private var currentImagePath: String = ""
    private var listOfImage: List<String> = emptyList()

    private lateinit var binding: FragmentAddEditTaskBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskViewPagerAdapter

    private val nav by lazy { findNavController() }
    private val taskId by lazy { arguments?.getInt("taskId", 0) ?: 0 }
    private val taskTitle by lazy { arguments?.getString("taskTitle", "") ?: "" }
    private val taskBody by lazy { arguments?.getString("taskBody", "") ?: "" }
    private val taskCompleted by lazy { arguments?.getBoolean("taskCompleted", false) ?: false }
    private val taskImagePath by lazy { arguments?.getStringArrayList("taskImagePath") }
    private val isEdit by lazy { arguments?.getInt("isEdit", 0) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        setAppBarTitle(isEdit)

        binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        adapter = TaskViewPagerAdapter{ holder, path ->

            if (!taskCompleted){
                holder.btnRemoveImg.setOnClickListener {
                    taskViewModel.removeImage(path)
                    loadImage()

                    if (listOfImage.isEmpty()){
                        binding.imgViewPager.isVisible = false
                        binding.imgPlaceHolder.isVisible = true
                    }

                }
            }else{ holder.btnRemoveImg.isVisible = false }

        }

        binding.imgViewPager.adapter = adapter

        if (isEdit == 0){
            binding.checkBoxCompleted.isVisible = false
            binding.imgViewPager.isVisible = false
            binding.txtTaskHeader.text = getString(R.string.add_task)
            binding.txtImg.text = getString(R.string.add_task_image)
            binding.btnAddUpdateTask.text = getString(R.string.add_task)
            setHasOptionsMenu(false)
        }else{
            loadTask()
            taskImagePath?.forEach { path -> Log.d(TAG, path) }
            if (taskCompleted){ setUneditable() }
            setHasOptionsMenu(true)
        }

        binding.btnAddImg.setOnClickListener { selectImage() }

        binding.btnAddUpdateTask.setOnClickListener { inputChecking() }

        return binding.root

    }

    private fun setUneditable() {

        binding.txtTitle.isEnabled = false
        binding.txtBody.isEnabled = false
        binding.btnAddImg.isVisible = false
        binding.checkBoxCompleted.isChecked = true

    }

    private fun setAppBarTitle(isEditValue: Int?) {

        val main = activity as MainActivity

        if (isEditValue == 0){ main.title = getString(R.string.addTaskLabel) }
        else{ main.title = getString(R.string.update_task_label) }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) { inflater.inflate(R.menu.delete_menu, menu) }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete){ confirmDeleteTask() }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmDeleteTask() {

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Delete Task?")
        builder.setMessage("Are you sure to delete task ${taskTitle}?")
        builder.setPositiveButton("Yes"){ _, _ -> deleteTask() }
        builder.setNegativeButton("No"){ _, _ ->  }
        builder.show()

    }

    private fun deleteTask() {

        val task = Task(taskId, taskTitle, taskBody, listOfImage,taskCompleted)

        taskViewModel.deleteTask(task)
        Log.d(TAG, "Task $taskTitle is deleted!",)
        Toast.makeText(requireContext(), "Task $taskTitle is successfully deleted!", Toast.LENGTH_SHORT).show()
        nav.navigate(R.id.action_addEditTaskFragment_to_taskListFragment)
    }

    private fun loadTask() {

        binding.txtTitle.setText(taskTitle)
        binding.txtBody.setText(taskBody)

        if (taskImagePath?.isEmpty() == true){
            binding.imgViewPager.isVisible = false
            binding.imgPlaceHolder.isVisible = true
        }else{
            binding.imgPlaceHolder.isVisible = false
            taskImagePath?.forEach { path -> taskViewModel.setImageList(path) }
            loadImage()
        }

    }

    private fun selectImage() {

        if (listOfImage.size <= 4){
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Add Task Image")
            builder.setMessage("Take Photo for Image or Select From Gallery?")
            builder.setPositiveButton("Open Camera"){ _, _ -> startCamera() }
            builder.setNegativeButton("Select From Gallery"){ _, _ -> openGallery() }
            builder.show()
        }else{
            Toast.makeText(requireContext(), "Cannot select more than 5 images!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun openGallery() {
        val selectImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        selectImageIntent.type = "image/*"

        requireActivity().startActivityFromFragment(this, Intent.createChooser(selectImageIntent, "Select Image"), REQUEST_SELECT_IMAGE)
    }

    private fun startCamera(){

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePicIntent ->

            takePicIntent.resolveActivity(requireContext().packageManager)?.also {
                val imageFile: File? = try { createImageFile() }
                catch (ex: IOException){
                    Log.e(TAG, "Unable to create file for image storage", ex)
                    null
                }

                imageFile?.also {
                    val imageUri: Uri = FileProvider.getUriForFile(requireContext(), "my.com.tasktodosample.fileprovider", it)
                    takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    requireActivity().startActivityFromFragment(this, takePicIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.CHINA).format(Date())
        val storageDir: File? = requireContext().filesDir

        return File.createTempFile("Image$timeStamp",".jpg", storageDir).apply { currentImagePath = absolutePath }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) { taskViewModel.setImageList(currentImagePath) }

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK){
            currentImagePath = data?.data.toString()
            taskViewModel.setImageList(currentImagePath)
        }

        binding.imgPlaceHolder.isVisible = false
        binding.imgViewPager.isVisible = true

        loadImage()

    }

    private fun loadImage(){

        taskViewModel.getImageList().observe(viewLifecycleOwner){
            adapter.setImageList(it)
            listOfImage = it
        }

        binding.imgViewPager.setCurrentItem(listOfImage.size - 1, true)

    }

    private fun inputChecking() {

        val title = binding.txtTitle.text.toString()
        val body = binding.txtBody.text.toString()

        if (title.isEmpty() || body.isEmpty()){ Toast.makeText(requireContext(), "Task Title and/or Body cannot be empty!", Toast.LENGTH_SHORT).show() }
        else{

            if (isEdit == 0){
                Log.d(TAG, "Now is adding task!")
                addTask(title,body,listOfImage)
            }else{
                Log.d(TAG, "Now is editing task!")
                val completed = binding.checkBoxCompleted.isChecked
                updateTask(title,body,listOfImage, completed)
            }

        }

    }

    private fun addTask(title: String, body: String, imagePath: List<String>) {

        if (imagePath.isEmpty()){ Log.d(TAG,"No Image selected or taken") }
        else{ imagePath.forEach { Log.d(TAG, "Image from $it is stored") } }

        val task = Task(0, title, body, imagePath, false)

        taskViewModel.addTask(task)

        Log.d(TAG, "Task $title added successful!")
        Toast.makeText(requireContext(), "Task $title is added!", Toast.LENGTH_SHORT).show()

        nav.navigate(R.id.action_addEditTaskFragment_to_taskListFragment)

    }

    private fun updateTask(title: String, body: String, imagePath: List<String>, completed: Boolean) {

        if (imagePath.isEmpty()){ Log.d(TAG,"No Image selected or taken") }
        else{ imagePath.forEach { Log.d(TAG, "Image from $it is stored") } }

        val task = Task(taskId, title, body, imagePath,completed)

        taskViewModel.updateTask(task)

        Log.d(TAG, "Task $title updated successful!")
        Toast.makeText(requireContext(), "Task $title is updated!", Toast.LENGTH_SHORT).show()

        nav.navigate(R.id.action_addEditTaskFragment_to_taskListFragment)

    }

}