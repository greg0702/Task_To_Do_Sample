package my.com.tasktodosample.fragments.addtask

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import my.com.tasktodosample.R
import my.com.tasktodosample.databinding.FragmentAddTaskBinding
import my.com.tasktodosample.model.Task
import my.com.tasktodosample.viewmodel.TaskViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddTaskFragment : Fragment() {

    private val TAG = "TASK_APP_TAG"
    private val REQUEST_IMAGE_CAPTURE: Int = 123
    private val REQUEST_SELECT_IMAGE: Int = 200
    private var currentImagePath: String = ""

    private lateinit var binding: FragmentAddTaskBinding
    private lateinit var taskViewModel: TaskViewModel

    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentAddTaskBinding.inflate(inflater, container, false)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        binding.btnRemoveImg.isVisible = false

        binding.addImgBtn.setOnClickListener { selectImage() }

        binding.btnAddTask.setOnClickListener { inputChecking() }

        return binding.root

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
                    val imageUri: Uri = FileProvider.getUriForFile(requireActivity(), "my.com.tasktodosample.fileprovider", it)
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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            binding.btnRemoveImg.isVisible = true
            Log.d(TAG, currentImagePath)
        }

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK){
            currentImagePath = data?.data.toString()
            binding.btnRemoveImg.isVisible = true
            Log.d(TAG, currentImagePath)
        }

        Glide.with(requireContext())
            .load(currentImagePath)
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

        binding.btnRemoveImg.setOnClickListener { removeImage() }

    }

    private fun removeImage() {
        binding.imgPreview.setImageResource(R.drawable.img_placeholder)
        binding.btnRemoveImg.isVisible = false
        currentImagePath = ""
    }

    private fun inputChecking() {

        val title = binding.txtTitle.text.toString()
        val body = binding.txtBody.text.toString()

        if (title.isEmpty() || body.isEmpty()){ Toast.makeText(requireContext(), "Task Title and/or Body cannot be empty!", Toast.LENGTH_SHORT).show() }
        else{ addTask(title,body,currentImagePath) }

    }

    private fun addTask(title: String, body: String, imagePath: String) {

        if (imagePath == ""){ Log.d(TAG,"No Image selected or taken") }
        else{ Log.d(TAG, "Image from $imagePath is stored") }

        val task = Task(0, title, body, imagePath, false)

        taskViewModel.addTask(task)

        Log.d(TAG, "Task $title added successful!")

        Toast.makeText(requireContext(), "Task $title is added!", Toast.LENGTH_SHORT).show()

        nav.navigate(R.id.action_addTaskFragment_to_taskListFragment)

    }

}