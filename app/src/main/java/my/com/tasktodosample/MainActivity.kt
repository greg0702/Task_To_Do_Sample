package my.com.tasktodosample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import my.com.tasktodosample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val nav by lazy { supportFragmentManager.findFragmentById(R.id.taskNav)!!.findNavController() }

    private lateinit var appBarConfig: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appBarConfig = AppBarConfiguration(setOf(R.id.taskListFragment, R.id.addTaskFragment),binding.drawerLayout)

        setupActionBarWithNavController(nav,appBarConfig)
        binding.navView.setupWithNavController(nav)

    }

    override fun onSupportNavigateUp(): Boolean {
        return nav.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

}