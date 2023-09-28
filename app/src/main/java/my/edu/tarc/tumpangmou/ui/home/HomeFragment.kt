package my.edu.tarc.tumpangmou.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentHomeBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
   // private lateinit var mainActivity2: MainActivity2


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val createPostViewModel: CreatePostViewModel by viewModels()

    //private lateinit var driverPost:DriverPost

    private lateinit var firebaseDatabase: FirebaseDatabase

    private var buttonStatus: Boolean = false

    private lateinit var bottomNavView: BottomNavigationView

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var postId:String


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseDatabase = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")

        getFirebaseData(object : FirebaseDataCallback {
            override fun onDriverPostDataReceived(driverPost: DriverPost?) {
                // Use the received data to update your UI or ViewModel
                driverPost?.let {
                    Log.d("onResume", it.status)
                    createPostViewModel.pendingDriverPost = it

                        binding.noRecordMessage.isVisible = false
                        binding.textViewDate.text = it.travelDate
                        binding.textViewTime.text = it.travelTime
                        binding.textViewDeparturePlace.text = it.departurePlace
                        binding.textViewTravellingPlace.text = it.travellingPlace
                        var points = it.driverPoints.toString()
                        binding.textViewDriverPoints.text = "$points points"
                        binding.textViewPassNum.text = it.passNum.toString()

                        binding.ridesPost.isVisible = true
                        binding.view4.isVisible = true
                        buttonStatus = false


                }
                if (driverPost == null){
                    binding.noRecordMessage.isVisible = true;
                    binding.ridesPost.isVisible = false;
                    binding.view4.isVisible = false;
                    binding.passengerTitle.isVisible= false;

                    val layoutParams = binding.buttonCreate.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.setMargins(0, 200.dpToPx(), 0, 0)
                    binding.buttonCreate.layoutParams = layoutParams

                    buttonStatus=true
                }
            }

            override fun onRequesterDataReceived(requesters: List<String>) {
                if (requesters.isEmpty()){
                    binding.passengerTitle.text = getString(R.string.no_request_message)
                    binding.passengerTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    val layoutParams = binding.passengerTitle.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.setMargins(50.dpToPx(), 300.dpToPx(), 0, 0)
                    binding.passengerTitle.layoutParams = layoutParams
                }
                else{

                }
                var nameStr = ""
                requesters.forEach{
                    nameStr += it
                }
            }
        })



        val createPostButton = binding.buttonCreate
        createPostButton.setOnClickListener {
            if (buttonStatus){
                findNavController().navigate(R.id.navigation_createPostFragment)
            }
            else{
                Toast.makeText(context, R.string.post_error, Toast.LENGTH_SHORT).show()
            }

        }

        binding.buttonComplete.setOnClickListener{

//            createPostViewModel.updateStatus(postId, "Completed")

            // Update status to "Completed"
            val newStatus = "Completed" // Replace with the new status value

            val postDetailsRef = FirebaseDatabase.getInstance().getReference("driver_post/$postId/post_details")

            val updates = HashMap<String, Any>()
            updates["status"] = newStatus

            postDetailsRef.updateChildren(updates)
                .addOnSuccessListener {

                    Toast.makeText(context, R.string.complete_post, Toast.LENGTH_SHORT).show()
                    binding.ridesPost.isVisible = false
                    binding.view4.isVisible = false;
                    binding.noRecordMessage.isVisible = true
                    binding.passengerTitle.isVisible = false

                    // Status updated successfully
                    // Add your code here if needed
                }
                .addOnFailureListener { error ->
                    // Failed to update status
                    // Handle the error or show a message to the user
                }

        }

        return root
    }


    override fun onResume() {
        super.onResume()
        getFirebaseData(object : FirebaseDataCallback {
            override fun onDriverPostDataReceived(driverPost: DriverPost?) {
                // Use the received data to update your UI or ViewModel
                driverPost?.let {
                    Log.d("onResume", it.status)
                    createPostViewModel.pendingDriverPost = it

                        binding.noRecordMessage.isVisible = false
                        binding.textViewDate.text = it.travelDate
                        binding.textViewTime.text = it.travelTime
                        binding.textViewDeparturePlace.text = it.departurePlace
                        binding.textViewTravellingPlace.text = it.travellingPlace
                        var points = it.driverPoints.toString()
                        binding.textViewDriverPoints.text = "$points points"
                        binding.textViewPassNum.text = it.passNum.toString()

                        binding.ridesPost.isVisible = true
                        binding.view4.isVisible = true
                        buttonStatus = false


                }
                if (driverPost == null){
                    Log.d("onResume", "null")

                    binding.noRecordMessage.isVisible = true;
                    binding.ridesPost.isVisible = false;
                    binding.view4.isVisible = false;
                    binding.passengerTitle.isVisible= false;

                    val layoutParams = binding.buttonCreate.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.setMargins(0, 200.dpToPx(), 0, 0)
                    binding.buttonCreate.layoutParams = layoutParams

                    buttonStatus=true
                }
            }

            override fun onRequesterDataReceived(requesters: List<String>) {
                if (requesters.isEmpty()){
                    binding.passengerTitle.text = getString(R.string.no_request_message)
                    binding.passengerTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    val layoutParams = binding.passengerTitle.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.setMargins(50.dpToPx(), 300.dpToPx(), 0, 0)
                    binding.passengerTitle.layoutParams = layoutParams
                }
                else{

                }
                var nameStr = ""
                requesters.forEach{
                    nameStr += it
                }
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        bottomNavView = binding.navView

        val navController = findNavController()
        bottomNavView.setupWithNavController(navController)


        getFirebaseData(object : FirebaseDataCallback {
            override fun onDriverPostDataReceived(driverPost: DriverPost?) {
                // Use the received data to update your UI or ViewModel
                driverPost?.let {
                    Log.d("onResume", it.status)
                    createPostViewModel.pendingDriverPost = it

                    binding.noRecordMessage.isVisible = false
                    binding.textViewDate.text = it.travelDate
                    binding.textViewTime.text = it.travelTime
                    binding.textViewDeparturePlace.text = it.departurePlace
                    binding.textViewTravellingPlace.text = it.travellingPlace
                    var points = it.driverPoints.toString()
                    binding.textViewDriverPoints.text = "$points points"
                    binding.textViewPassNum.text = it.passNum.toString()

                    binding.ridesPost.isVisible = true
                    binding.view4.isVisible = true
                    buttonStatus = false


                }
                if (driverPost == null){
                    Log.d("onResume", "null")

                    binding.noRecordMessage.isVisible = true;
                    binding.ridesPost.isVisible = false;
                    binding.view4.isVisible = false;
                    binding.passengerTitle.isVisible= false;

                    val layoutParams = binding.buttonCreate.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.setMargins(0, 200.dpToPx(), 0, 0)
                    binding.buttonCreate.layoutParams = layoutParams

                    buttonStatus=true
                }
            }

            override fun onRequesterDataReceived(requesters: List<String>) {
                if (requesters.isEmpty()){
                    binding.passengerTitle.text = getString(R.string.no_request_message)
                    binding.passengerTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    val layoutParams = binding.passengerTitle.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.setMargins(50.dpToPx(), 300.dpToPx(), 0, 0)
                    binding.passengerTitle.layoutParams = layoutParams
                }
                else{

                }
                var nameStr = ""
                requesters.forEach{
                    nameStr += it
                }
            }
        })


//        // Set up the AppBarConfiguration
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_driver, R.id.navigation_rides, R.id.navigation_history, R.id.navigation_account
//            )
//        )
//
//        // Set up the ActionBar with the NavController and AppBarConfiguration
//        (requireActivity() as AppCompatActivity).setupActionBarWithNavController(navController, appBarConfiguration)
//
//        // Set up the BottomNavigationView with the NavController
//        binding.navView.setupWithNavController(navController)
//
//        // Disable the BottomNavigationView
//        binding.navView.isEnabled = false
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bottom_nav_menu, menu)
    }


    //get the pending post only
    private fun getFirebaseData(callback: FirebaseDataCallback) {


        firebaseDatabase.getReference("driver_post").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var matchingDriverPost: DriverPost? = null

                for (postSnapshot in snapshot.children) {
                    val postDetailsSnapshot = postSnapshot.child("post_details")
                    val driverPost = postDetailsSnapshot.getValue(DriverPost::class.java)

                    Log.d("HomeFragment", "home passed....")

                    if (driverPost != null && driverPost.status == "Incomplete" && driverPost.driverID == userId) {
                        Log.d("HomeFragment", ".selected")

                        val newDriverPost = DriverPost(
                            driverPost.id,
                            driverPost.departurePlace,
                            driverPost.travellingPlace,
                            driverPost.travelDate,
                            driverPost.travelTime,
                            driverPost.carPlateNum,
                            driverPost.carModel,
                            driverPost.carColor,
                            driverPost.passNum,
                            driverPost.passCount,
                            driverPost.status,
                            driverPost.driverPoints,
                            driverPost.passengerPoints,
                            driverPost.driverID,
                            null
                        )
                        postId = driverPost.id
                        matchingDriverPost = newDriverPost
                        break // Exit the loop after finding the first matching driver post
                    }
                }

                if (matchingDriverPost != null) {
                    callback.onDriverPostDataReceived(matchingDriverPost)
                    // Update createPostViewModel or perform other operations with the found driver post
                } else {
                    callback.onDriverPostDataReceived(null)
                    // Handle case when no matching driver post is found
                    Log.d("HomeFragment", "No matching driver post found")
                    // Perform appropriate action, such as displaying a message to the user
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error Loading Data", Toast.LENGTH_SHORT).show()
            }
        })

//
//        firebaseDatabase.getReference("/driver_post/$pendingPostID/post_requesters").addValueEventListener(object : ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val requesters = ArrayList<String>()
//                    if (snapshot.exists()) {
//                        for (childSnapshot in snapshot.children) {
//                            val requesterName = childSnapshot.getValue(String::class.java)!!
//                            requesters.add(requesterName)
//                        }
//                        createPostViewModel.pendingDriverPost?.requester = requesters
//
//
//                    }else{
//                        createPostViewModel.pendingDriverPost?.requester = null
//                    }
//                    // Call the callback function with the received data
//                    callback.onRequesterDataReceived(requesters)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(requireContext(), "Error Loading Data", Toast.LENGTH_SHORT).show()
//                }
//
//            })
//
//        }
    }

    fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface FirebaseDataCallback {
        fun onDriverPostDataReceived(driverPost: DriverPost?)
        fun onRequesterDataReceived(requesters: List<String>)
    }
}