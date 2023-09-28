package my.edu.tarc.tumpangmou

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import my.edu.tarc.tumpangmou.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private var isLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPrefs = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        // Retrieve login status from SharedPreferences
        isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false)

        // Initialize the NavHostFragment and NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

//        val getter = sharedPrefs.getBoolean("isLoggedIn", false)
//        Toast.makeText(this, getter.toString(), Toast.LENGTH_SHORT).show()

        if (isLoggedIn) {
            // The app was launched from outside (e.g. a notification or deep link)
            // Navigate to the HomeFragment
//            Toast.makeText(this, userId, Toast.LENGTH_SHORT).show()
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_indexFragment_to_navigation_driver)
        } else if (!isLoggedIn) {
            // The app was launched from outside (e.g. a notification or deep link)
            // Navigate to the IndexFragment
            findNavController(R.id.nav_host_fragment).navigate(R.id.indexFragment)

        }


    }

}
