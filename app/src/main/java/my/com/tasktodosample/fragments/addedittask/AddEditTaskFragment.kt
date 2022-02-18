package my.com.tasktodosample.fragments.addedittask

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import my.com.tasktodosample.MainActivity
import my.com.tasktodosample.R
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

    private lateinit var binding: FragmentAddEditTaskBinding
    private lateinit var taskViewModel: TaskViewModel

    private val nav by lazy { findNavController() }
    private val taskId by lazy { arguments?.getInt("taskId", 0) ?: 0 }
    private val taskTitle by lazy { arguments?.getString("taskTitle", "") ?: "" }
    private val taskBody by lazy { arguments?.getString("taskBody", "") ?: "" }
    private val taskImagePath by lazy { arguments?.getString("taskImagePath", "") ?: "" }
    private val taskCompleted by lazy { arguments?.getBoolean("taskCompleted", false) ?: false }
    private val testList by lazy { arguments?.getStringArrayList("testList") }
    private val isEdit by lazy { arguments?.getInt("isEdit", 0) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        setAppBarTitle(isEdit)

        binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        if (isEdit == 0){

            binding.checkBoxCompleted.isVisible = false
            binding.txtTaskHeader.text = getString(R.string.add_task)
            binding.txtImg.text = getString(R.string.add_task_image)
            binding.btnRemoveImg.isVisible = false
            binding.btnAddUpdateTask.text = getString(R.string.add_task)
            setHasOptionsMenu(false)

        }else{
            loadTask()

            if (taskCompleted){
                setUneditable()
            }

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
        binding.btnRemoveImg.isVisible = false

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

        val task = Task(taskId, taskTitle, taskBody, taskImagePath,taskCompleted)

        taskViewModel.deleteTask(task)
        Log.d(TAG, "Task $taskTitle is deleted!",)
        Toast.makeText(requireContext(), "Task $taskTitle is successfully deleted!", Toast.LENGTH_SHORT).show()
        nav.navigate(R.id.action_addEditTaskFragment_to_taskListFragment)
    }

    private fun loadTask() {

        binding.txtTitle.setText(taskTitle)
        binding.txtBody.setText(taskBody)
        currentImagePath = taskImagePath

        loadImage(currentImagePath)

        if (taskCompleted){ binding.btnRemoveImg.isVisible = false }

    }

    private fun selectImage() {

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Add Task Image")
        builder.setMessage("Take Photo for Image or Select From Gallery?")
        builder.setPositiveButton("Open Camera"){ _, _ -> startCamera() }
        builder.setNegativeButton("Select From Gallery"){ _, _ -> openGallery() }
        builder.show()

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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            binding.btnRemoveImg.isVisible = true
            Log.d(TAG, currentImagePath)
        }

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK){
            currentImagePath = data?.data.toString()
            binding.btnRemoveImg.isVisible = true
            Log.d(TAG, currentImagePath)
        }

        loadImage(currentImagePath)

    }

    private fun removeImage() {
        binding.imgPreview.setImageResource(R.drawable.img_placeholder)
        binding.btnRemoveImg.isVisible = false
        currentImagePath = ""
    }

    private fun loadImage(path: String){

        if (path == ""){
            Log.d(TAG, "There is no image")
            binding.imgPreview.setImageResource(R.drawable.img_placeholder)
            binding.btnRemoveImg.isVisible = false
        }else{
            Log.d(TAG, "There is a image")
            Glide.with(requireContext())
                .load(path)
                .placeholder(R.drawable.img_placeholder)
                .fitCenter()
                .error(R.drawable.ic_error_loading)
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        Log.e(TAG, "Image loading error. ${e.toString()}", e)
                        Toast.makeText(requireContext(), "Error loading image preview", Toast.LENGTH_SHORT).show()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        Log.d(TAG, "Image loaded successfully")
                        return false
                    }
                })
                .into(binding.imgPreview)
            binding.btnRemoveImg.isVisible = true

            binding.btnRemoveImg.setOnClickListener { removeImage() }

        }

    }

    private fun inputChecking() {

        val title = binding.txtTitle.text.toString()
        val body = binding.txtBody.text.toString()

        if (title.isEmpty() || body.isEmpty()){ Toast.makeText(requireContext(), "Task Title and/or Body cannot be empty!", Toast.LENGTH_SHORT).show() }
        else{

            if (isEdit == 0){
                Log.d(TAG, "Now is adding task!")
                addTask(title,body,currentImagePath)
            }else{
                Log.d(TAG, "Now is editing task!")
                val completed = binding.checkBoxCompleted.isChecked
                updateTask(title,body,currentImagePath, completed)
            }

        }

    }

    private fun addTask(title: String, body: String, imagePath: String) {

        if (imagePath == ""){ Log.d(TAG,"No Image selected or taken") }
        else{ Log.d(TAG, "Image from $imagePath is stored") }

        val task = Task(0, title, body, imagePath, false)

        taskViewModel.addTask(task)

        Log.d(TAG, "Task $title added successful!")
        Toast.makeText(requireContext(), "Task $title is added!", Toast.LENGTH_SHORT).show()

        nav.navigate(R.id.action_addEditTaskFragment_to_taskListFragment)

    }

    private fun updateTask(title: String, body: String, imagePath: String, completed: Boolean) {

        if (imagePath == ""){ Log.d(TAG,"No Image selected or taken") }
        else{ Log.d(TAG, "Image from $imagePath is stored") }

        val task = Task(taskId, title, body, imagePath,completed)

        taskViewModel.updateTask(task)

        Log.d(TAG, "Task $title updated successful!")
        Toast.makeText(requireContext(), "Task $title is updated!", Toast.LENGTH_SHORT).show()

        nav.navigate(R.id.action_addEditTaskFragment_to_taskListFragment)

    }

}