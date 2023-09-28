package my.edu.tarc.tumpangmou.ui.rides

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentSearchPostBinding
import java.util.Calendar

class SearchPostFragment : Fragment() {
    private var _binding: FragmentSearchPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPostBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        fun newInstance() = SearchPostFragment()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSearchPost.setOnClickListener {
            // Prompt error if input fields are empty and restrict the user from proceeding
            if (binding.spinnerSearchDeparture.selectedItem.toString() == "Select your travelling place"
                || binding.spinnerSearchDestination.selectedItem.toString() == "Select your departure place"
                || binding.editTextDate.text.toString().isEmpty()) {
                val toast = Toast.makeText(requireContext(), "Please fill in the necessary details", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 250)
                toast.show()
                return@setOnClickListener
            } else {
                searchRides()
            }
        }

        binding.editTextDate.setOnClickListener {
            // create a calendar instance with the current date as the default
            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(requireContext())
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000

            // create a date picker dialog with the current date as the default
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // set the selected date to the input field
                    val selectedDate = "$year-${month + 1}-$dayOfMonth"

                    binding.editTextDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            // set the minimum date to today
            datePickerDialog.datePicker.minDate = datePicker.datePicker.minDate

            // show the date picker dialog to the user
            datePickerDialog.show()
        }

        bottomNavView = binding.navView

        val navController = findNavController()
        bottomNavView.setupWithNavController(navController)
    }

    private fun searchRides() {
        val databaseRef = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("driver_post")

        databaseRef.get().addOnSuccessListener { snapshot: DataSnapshot ->
            // Parse the ride data from Firebase and store it in a list
            val rideList = arrayListOf<RidesPost>()
            for (postSnapshot in snapshot.children) {
                val ride = postSnapshot.children.first().getValue(RidesPost::class.java)
                rideList.add(ride!!)
            }

            // Filter the rideList based on the source and destination selected by the user
            val filteredRidesPlace = rideList.filter { it.departurePlace == binding.spinnerSearchDeparture.selectedItem.toString()
                    && it.travellingPlace == binding.spinnerSearchDestination.selectedItem.toString()
                    && it.travelDate == binding.editTextDate.text.toString()}

            // Remove completed rides from the list
            val filteredRides = filteredRidesPlace.filter { it.status == "Incomplete" }
            Log.d("SearchPostFragment", "searchRides: $filteredRides")

            // Convert the list of RidesPost objects to a list of Parcelable objects
            val filteredRidesParcelable = filteredRides.map { it as Parcelable }.toList()

            // Pass the filteredRides to the fragment where they will be displayed
            val bundle = Bundle()
            bundle.putParcelableArrayList("rides", ArrayList(filteredRidesParcelable))
            Log.d("SearchPostFragment", "searchRides: $bundle")    // for debugging purposes
            findNavController().navigate(R.id.action_navigation_rides_to_loadPostFragment, bundle)
        }
//        databaseRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // Parse the ride data from Firebase and store it in a list
//                val rideList = arrayListOf<RidesPost>()
//                for (postSnapshot in snapshot.children) {
//                    val ride = postSnapshot.children.first().getValue(RidesPost::class.java)
//                    rideList.add(ride!!)
//                }
//
//                // Filter the rideList based on the source and destination selected by the user
//                val filteredRidesPlace = rideList.filter { it.departurePlace == binding.spinnerSearchDeparture.selectedItem.toString()
//                        && it.travellingPlace == binding.spinnerSearchDestination.selectedItem.toString()
//                        && it.travelDate == binding.editTextDate.text.toString()}
//
//                // Remove completed rides from the list
//                val filteredRides = filteredRidesPlace.filter { it.status != "Completed" }
//
//                // Convert the list of RidesPost objects to a list of Parcelable objects
//                val filteredRidesParcelable = filteredRides.map { it as Parcelable }.toList()
//
//                // Pass the filteredRides to the fragment where they will be displayed
//                val bundle = Bundle()
//                bundle.putParcelableArrayList("rides", ArrayList(filteredRidesParcelable))
//                Log.d("SearchPostFragment", "searchRides: $bundle")    // for debugging purposes
//                findNavController().navigate(R.id.action_navigation_rides_to_loadPostFragment, bundle)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle error
//            }
//        })
    }
}