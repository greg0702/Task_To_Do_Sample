package my.com.tasktodosample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import my.com.tasktodosample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val nav by lazy { supportFragmentManager.findFragmentById(R.id.taskNav)!!.findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBarWithNavController(nav)

    }

    override fun onSupportNavigateUp(): Boolean { return nav.navigateUp() || super.onSupportNavigateUp() }

}