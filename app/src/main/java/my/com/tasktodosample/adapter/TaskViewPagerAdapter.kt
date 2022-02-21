package my.com.tasktodosample.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import my.com.tasktodosample.R

class TaskViewPagerAdapter(
    val fn: (ViewHolder, String) -> Unit = { _, _ -> }
): RecyclerView.Adapter<TaskViewPagerAdapter.ViewHolder>() {

    private val TAG = "TASK_APP_TAG"
    private var imageList: List<String> = emptyList()

    class ViewHolder(view: View, val context: Context): RecyclerView.ViewHolder(view){

        val imgPreview: ImageView = view.findViewById(R.id.imgPreview)
        val btnRemoveImg: ImageView = view.findViewById(R.id.btnRemoveImg)

    }

    fun setImageList(imgList: List<String>){

        this.imageList = imgList

        this.imageList.forEach {
            notifyItemInserted(this.imageList.indexOf(it))
            notifyItemChanged(this.imageList.indexOf(it))
            notifyItemRemoved(this.imageList.indexOf(it))
        }

        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentImage = imageList[position]

        Glide.with(holder.context)
                .load(currentImage)
                .placeholder(R.drawable.img_placeholder)
                .fitCenter()
                .error(R.drawable.ic_error_loading)
                .listener(object: RequestListener<Drawable> {

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        Log.e(TAG, "Image loading error. ${e.toString()}", e)
                        Toast.makeText(holder.context, "Error loading image preview", Toast.LENGTH_SHORT).show()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        Log.d(TAG, "Image loaded successfully")
                        return false
                    }
                })
                .into(holder.imgPreview)

        imageList.forEach { Log.d(TAG, "Current image path is $it") }

        fn(holder,currentImage)

    }

    override fun getItemCount(): Int {
        return imageList.size
    }

}