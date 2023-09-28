package my.edu.tarc.tumpangmou.ui.rides

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tar.mobileassignment.LoadPostAdapter
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentLoadPostBinding
import java.io.Serializable

class LoadPostFragment : Fragment(), LoadPostPopUpWindowFragment.OnFragmentInteractionListener {
    private var _binding: FragmentLoadPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var postRecyclerView : RecyclerView
    private var postArrayList: ArrayList<RidesPost> = arrayListOf<RidesPost>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadPostBinding.inflate(inflater, container, false)

        // Get the bundle passed from the previous fragment
        val bundle = arguments

        // Check if the bundle is not null
        if (bundle != null) {
            postArrayList = bundle.getSerializable("rides") as ArrayList<RidesPost>
        }

        // Set the recycler view
        postRecyclerView = binding.recyclerViewLoadPost
        postRecyclerView.layoutManager = LinearLayoutManager(context)
        postRecyclerView.setHasFixedSize(true)

        // Get the data from the database
        getPostData()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLoadPostSearch.setOnClickListener{
            val loadPostPopUpWindowFragment = LoadPostPopUpWindowFragment()
            loadPostPopUpWindowFragment.listener = this
            loadPostPopUpWindowFragment.show(parentFragmentManager, "LoadPostPopUpWindowFragment")
        }
    }

    private fun getPostData() {
        Log.d("LoadPostFragment", "$postArrayList")
        // Set the adapter
        val postAdapter = LoadPostAdapter(postArrayList)

        // If the book button clicked, add booking to the database
        postAdapter.onBookButtonClickListener = { item ->
            addBooking(item)
            // Toast for booking made with passCount
            Toast.makeText(context, "Booking made", Toast.LENGTH_SHORT).show()
        }

        postRecyclerView.adapter = postAdapter
    }

    private fun addBooking(ridesPost: RidesPost) {
        // Get user id from shared preferences
        val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString("userId", "defaultUserId")

        // Add data into database
        val db = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")
        try {
            val newBookingRef = db.getReference("bookings").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookingId = snapshot.childrenCount.toInt() + 1
                    db.getReference("bookings").child(bookingId.toString()).setValue(mapOf(
                        "postId" to ridesPost.id,
                        "driverId" to ridesPost.driverID,
                        "passengerId" to userId,
                        "driverPoints" to ridesPost.driverPoints,
                        "passengerPoints" to ridesPost.passengerPoints,
                        "carPlateNum" to ridesPost.carPlateNum,
                        "carModel" to ridesPost.carModel,
                        "carColor" to ridesPost.carColor,
                        "source" to ridesPost.departurePlace,
                        "destination" to ridesPost.travellingPlace,
                        "date" to ridesPost.travelDate,
                        "time" to ridesPost.travelTime
                    ))
                }

                override fun onCancelled(error: DatabaseError) {
                    // TODO
                }
            })
            Log.d("LoadPostFragment", "Booking added: $newBookingRef")
        } catch (e: Exception) {
            Log.d("LoadPostFragment", "Error adding booking: $e")
        }

        try {
            db.getReference("driver_post").child(ridesPost.id).child("post_details").child("passCount").setValue(ridesPost.passCount + 1)
            Log.d("LoadPostFragment", "Passenger count updated")
        } catch (e: Exception) {
            Log.d("LoadPostFragment", "Error updating passenger count: $e")
        }

        if (ridesPost.passCount + 1 == ridesPost.passNum) {
            // Change post status to booked
            try {
                db.getReference("driver_post").child(ridesPost.id).child("post_details").child("status").setValue("Booked")
                Log.d("LoadPostFragment", "Post status updated")
            } catch (e: Exception) {
                Log.d("LoadPostFragment", "Error updating post status: $e")
            }
        }
        fetchData()
    }

    private fun fetchData () {
        val databaseRef = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("driver_post")

        databaseRef.get().addOnSuccessListener { snapshot: DataSnapshot ->
            // Parse the ride data from Firebase and store it in a list
            val rideList = arrayListOf<RidesPost>()
            for (postSnapshot in snapshot.children) {
                val ride = postSnapshot.children.first().getValue(RidesPost::class.java)
                rideList.add(ride!!)
            }

            // Remove completed rides from the list
            val filteredRides = rideList.filter { it.status == "Incomplete" && it.departurePlace == postArrayList[0].departurePlace && it.travellingPlace == postArrayList[0].travellingPlace }
            postArrayList = filteredRides as ArrayList<RidesPost>
            getPostData()
        }
    }

    override fun onBundlePass(bundle: Bundle) {
        Log.d("LoadPostFragment", "onBundlePass: $bundle")
        postArrayList = bundle.getSerializable("rides") as ArrayList<RidesPost>
        getPostData()
    }
}