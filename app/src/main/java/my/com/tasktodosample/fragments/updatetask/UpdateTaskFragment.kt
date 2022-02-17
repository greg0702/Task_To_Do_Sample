package my.com.tasktodosample.fragments.updatetask

import android.app.Activity
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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import my.com.tasktodosample.MainActivity
import my.com.tasktodosample.R
import my.com.tasktodosample.databinding.FragmentUpdateTaskBinding
import my.com.tasktodosample.model.Task
import my.com.tasktodosample.viewmodel.TaskViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class UpdateTaskFragment : Fragment() {

    private val args by navArgs<UpdateTaskFragmentArgs>()

    private val TAG = "ToDoApp"
    private val REQUEST_IMAGE_CAPTURE: Int = 123
    private val REQUEST_SELECT_IMAGE: Int = 200
    private var currentImagePath: String = ""

    private lateinit var binding: FragmentUpdateTaskBinding
    private lateinit var taskViewModel: TaskViewModel

    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentUpdateTaskBinding.inflate(inflater, container, false)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        lockDrawer()

        loadTask()

        binding.updateImgBtn.setOnClickListener { selectImage() }

        binding.btnUpdateTask.setOnClickListener { inputChecking() }

        return binding.root

    }

    private fun loadTask() {

        binding.txtUpdateTitle.setText(args.currentTask.taskTitle)
        binding.txtUpdateBody.setText(args.currentTask.taskBody)

        loadImage(args.currentTask.taskImage)

        binding.btnRemoveUpdateImg.setOnClickListener { removeImage() }

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

            binding.btnRemoveUpdateImg.isVisible = true

            Log.d(TAG, currentImagePath)
        }

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK){
            currentImagePath = data?.data.toString()

            binding.btnRemoveUpdateImg.isVisible = true

            Log.d(TAG, currentImagePath)
        }

        loadImage(currentImagePath)

        binding.btnRemoveUpdateImg.setOnClickListener { removeImage() }

    }

    private fun removeImage() {
        binding.imgUpdatePreview.setImageResource(R.drawable.img_placeholder)
        binding.btnRemoveUpdateImg.isVisible = false
    }

    private fun loadImage(path: String){

        if (path == ""){
            Log.d(TAG, "There is no image")
            binding.imgUpdatePreview.setImageResource(R.drawable.img_placeholder)
            binding.btnRemoveUpdateImg.isVisible = false
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
                .into(binding.imgUpdatePreview)

            binding.btnRemoveUpdateImg.isVisible = true
        }

    }

    private fun inputChecking() {

        val title = binding.txtUpdateTitle.text.toString()
        val body = binding.txtUpdateBody.text.toString()

        if (title.isEmpty() || body.isEmpty()){
            Toast.makeText(requireContext(), "Task Title and/or Body cannot be empty!", Toast.LENGTH_SHORT).show()
        }else{ updateTask(title,body,currentImagePath) }

    }

    private fun updateTask(title: String, body: String, imagePath: String) {

        if (imagePath == ""){
            Log.d(TAG,"No Image selected or taken")
        }else{
            Log.d(TAG, "Image from $imagePath is stored")
        }

        val task = Task(args.currentTask.id, title, body, imagePath)

        taskViewModel.updateTask(task)

        Log.d(TAG, "Task $title updated successful!")

        Toast.makeText(requireContext(), "Task $title is updated!", Toast.LENGTH_SHORT).show()

        nav.navigate(R.id.action_updateTaskFragment_to_taskListFragment)

    }

    private fun lockDrawer(){
        val main = activity as MainActivity
        main.setDrawerLocked()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lockDrawer()
    }

}