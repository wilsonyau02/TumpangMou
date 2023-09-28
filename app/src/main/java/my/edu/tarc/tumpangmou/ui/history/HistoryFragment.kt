package my.edu.tarc.tumpangmou.ui.history

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.tumpangmou.PostAdapter
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentHistoryBinding
import my.edu.tarc.tumpangmou.ui.home.CreatePostViewModel
import my.edu.tarc.tumpangmou.ui.home.DriverPost

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var dbref : DatabaseReference
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postArrayList : ArrayList<DriverPost>

    private lateinit var firebaseDatabase: FirebaseDatabase

    private val createPostViewModel: CreatePostViewModel by viewModels()


    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var adapter:PostAdapter
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(HistoryViewModel::class.java)

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root



        firebaseDatabase = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")



        binding.buttonPassenger.setOnClickListener{
            binding.buttonPassenger.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
            binding.buttonPassenger.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.buttonPassenger.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)


            binding.buttonDriver.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
            binding.buttonDriver.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            binding.buttonDriver.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)


            //TODO: SHOW HISTORY
        }

        binding.buttonDriver.setOnClickListener{
            binding.buttonDriver.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
            binding.buttonDriver.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.buttonDriver.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)



            binding.buttonPassenger.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
            binding.buttonPassenger.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            binding.buttonPassenger.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)

            //TODO: SHOW HISTORY

        }



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        bottomNavView = binding.navView

        val navController = findNavController()
        bottomNavView.setupWithNavController(navController)


        adapter = PostAdapter()
        binding.historyList.adapter = adapter
        binding.historyList.layoutManager = LinearLayoutManager(requireContext())

        getDriverHistory()
    }





    private fun getDriverHistory() {
        createPostViewModel.deleteAllDriverPost()
        dbref = FirebaseDatabase.getInstance().getReference("driver_post")
        val status = "Completed"

        firebaseDatabase.getReference("driver_post").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val postId = userSnapshot.key
                        firebaseDatabase.getReference("driver_post/$postId/post_details").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val postDetails = snapshot.getValue(DriverPost::class.java)
                                    if (postDetails?.driverID == userId && postDetails.status == status) {
                                        createPostViewModel.insertDriverPost(postDetails)
                                        updateUI()
                                    }
                                }
                                Log.d("HistoryFragment", "fuck u")

                                // Check if this is the last snapshot
                                if (userSnapshot == snapshot.children.last()) {
                                    // Data retrieval is complete, update the UI
                                    Log.d("HistoryFragment", "Data retrieval is complete")
                                    updateUI()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("HistoryFragment", "No bug please")
                                // Handle error
                            }
                        })
                    }
                } else {
                    // No data available, update the UI
                    Log.d("HistoryFragment", "No data available")

                    updateUI()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    private fun updateUI() {
        // Retrieve the updated list of DriverPosts from the ViewModel
        createPostViewModel.driverPostList.observe(viewLifecycleOwner, Observer { driverPosts ->
            Log.d("HistoryFragment", "Driver Post List Size: ${driverPosts.size}")
            Log.d("updateUI", "i am here")
            if (driverPosts.isEmpty()) {
                Log.d("updateUI", "null here")
                binding.textNotifications.isVisible = true
            } else {
                binding.textNotifications.isVisible = false
            }
            adapter.setDriverPost(driverPosts)
            adapter.notifyDataSetChanged()
        })
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bottom_nav_menu, menu)
    }

}