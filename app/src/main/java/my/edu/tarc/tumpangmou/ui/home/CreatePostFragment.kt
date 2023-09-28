package my.edu.tarc.tumpangmou.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import my.edu.tarc.tumpangmou.databinding.FragmentCreatePostBinding
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null

    companion object {
        fun newInstance() = CreatePostFragment()
    }

    private val createPostViewModel: CreatePostViewModel by viewModels()

    private val binding get() = _binding!!


    private lateinit var editText: EditText

    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private var postId:String = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        binding.editTextTravellingDate.setOnClickListener {
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

                    binding.editTextTravellingDate.setText(selectedDate)
                    Log.d("testdate", selectedDate)

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

        binding.editTextTravellingTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minuteOfDay ->
                    // Handle selected time here
                    val timeStr = String.format("%02d:%02d", hourOfDay, minuteOfDay)
                    binding.editTextTravellingTime.setText(timeStr)


                },
                hour,
                minute,
                DateFormat.is24HourFormat(requireContext())
            )

            timePickerDialog.show()

        }

        binding.buttonPost.setOnClickListener {
            try {

                val departPlaceIndex = binding.spinnerDeparturePlace.selectedItemPosition
                val travelPlaceIndex = binding.spinnerTravelPlace.selectedItemPosition
                if (departPlaceIndex == 0 || travelPlaceIndex == 0) {
                    Toast.makeText(context, "Please select one place", Toast.LENGTH_SHORT).show()
                } else {
                    val travelDate = binding.editTextTravellingDate.text.toString()
                    val travelTime = binding.editTextTravellingTime.text.toString()
                    val carPlate = binding.editTextCarPlateNum.text.toString()
                    val carModel = binding.editTextCarModel.text.toString()
                    val carColor = binding.editTextCarColor.text.toString()
                    val passNum = Integer.parseInt(binding.editTextPassengerNum.text.toString())
                    val status = "Incomplete"
                    val (driverPoints, passengerPoints) = generateRandomNumber()

                    firebaseDatabase = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")

                    firebaseDatabase.getReference("driver_post").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val postCount = snapshot.childrenCount
                            Log.d("Testing", postCount.toString())
                            val count = postCount.toInt()
                            val postId = "DP${count + 1}"

                            val database = firebaseDatabase.getReference("driver_post")

                            database.child(postId).child("post_details").setValue(
                                mapOf(
                                    "id" to postId,
                                    "departurePlace" to binding.spinnerDeparturePlace.selectedItem.toString(),
                                    "travellingPlace" to binding.spinnerTravelPlace.selectedItem.toString(),
                                    "travelDate" to travelDate,
                                    "travelTime" to travelTime,
                                    "carPlateNum" to carPlate,
                                    "carModel" to carModel,
                                    "carColor" to carColor,
                                    "passNum" to passNum,
                                    "passCount" to 0,
                                    "status" to status,
                                    "driverPoints" to driverPoints,
                                    "passengerPoints" to passengerPoints,
                                    "driverID" to userId
                                )
                            )

                            val newDriverPost = DriverPost(
                                postId,
                                binding.spinnerDeparturePlace.selectedItem.toString(),
                                binding.spinnerTravelPlace.selectedItem.toString(),
                                travelDate,
                                travelTime,
                                carPlate,
                                carModel,
                                carColor,
                                passNum,
                                0,
                                status,
                                driverPoints,
                                passengerPoints,
                                userId,
                                null
                            )

                            Log.d("Driver", newDriverPost.id)
                            createPostViewModel.insertDriverPost(newDriverPost)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle onCancelled if needed
                        }
                    })

                    // createPost(newDriverPost)

//                    createPostViewModel.postLenth++
//                    createPostViewModel.pendingPost = id
//                    createPostViewModel.postIdList.add(id)
//                    createPostViewModel.pendingDriverPost = newDriverPost

                    Log.d("create post", "id: " + createPostViewModel.pendingPost)

                    Toast.makeText(context, "Post Created", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }


//                requireActivity().onBackPressed()
            } catch (e: Exception) {
                // handle the exception here
                e.printStackTrace()
                Log.d("Error", e.toString())
                Toast.makeText(context, "Error creating post", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonCancel.setOnClickListener {
            binding.spinnerDeparturePlace.setSelection(0)
            binding.spinnerTravelPlace.setSelection(0)
            binding.editTextTravellingDate.text.clear()
            binding.editTextTravellingTime.text.clear()
            binding.editTextCarPlateNum.text.clear()
            binding.editTextCarModel.text.clear()
            binding.editTextCarColor.text.clear()
            binding.editTextPassengerNum.text.clear()

        }


        // Set up the listener for the soft keyboard
//
//        val activityRootView =
//            requireActivity().window.decorView.findViewById<View>(android.R.id.content)
//
//        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
//            if (isAdded) {
//                val rect = Rect()
//                view?.getWindowVisibleDisplayFrame(rect)
//                val heightDifference = view?.rootView?.height?.minus(rect.height())
//                if (heightDifference != null) {
//                    if (heightDifference > dpToPx(
//                            requireContext(),
//                            250F
//                        )
//                    ) { // Adjust the value of 200 according to your layout
//                        editText.postDelayed({
//                            val scrollY = heightDifference - editText.height
//                            if (scrollY > 0) {
//                                activityRootView.scrollTo(0, scrollY)
//                            }
//
//                            // Add the code to hide the keyboard here
//                            val activity = activity
//                            if (isAdded && activity != null) {
//                                val imm =
//                                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                                imm.hideSoftInputFromWindow(view?.windowToken, 0)
//                            }
//
//                        }, 100)
//                    } else {
//                        activityRootView.scrollTo(0, 0)
//                    }
//                }
//            }
//
//        }
//
//        // Hide the soft keyboard
//        val activity = activity
//        if (isAdded && activity != null) {
//            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(view?.windowToken, 0)
//        }
//
//        view?.viewTreeObserver?.addOnGlobalLayoutListener(globalLayoutListener)

        return binding.root


    }

    private fun createPost(driverPost: DriverPost) {

        val database =
            Firebase.database("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app").reference

        // Generate a new ID for the post if it does not exist
        val postId = if (driverPost.id.isEmpty()) {
            database.child("driver_post").push().key ?: return
        } else {
            driverPost.id
        }


        database.child("driver_post").child(driverPost.id).child("post_details").setValue(
            mapOf(
                "id" to driverPost.id,
                "departurePlace" to driverPost.departurePlace,
                "travellingPlace" to driverPost.travellingPlace,
                "travelDate" to driverPost.travelDate,
                "travelTime" to driverPost.travelTime,
                "carPlateNum" to driverPost.carPlateNum,
                "carModel" to driverPost.carModel,
                "carColor" to driverPost.carColor,
                "passNum" to driverPost.passNum,
                "passCount" to driverPost.passCount,
                "status" to driverPost.status,
                "driverPoints" to driverPost.driverPoints,
                "passengerPoints" to driverPost.passengerPoints,
                "driverID" to driverPost.driverID
            )
        )


    }


    override fun onDestroyView() {
        super.onDestroyView()
//        if (globalLayoutListener != null) {
//            view?.viewTreeObserver?.removeOnGlobalLayoutListener(globalLayoutListener)
//            globalLayoutListener = null
//        }

    }

    private fun dpToPx(context: Context, dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }


    private fun generateRandomNumber(): Pair<Int, Int> {
        val arr = intArrayOf(300, 400, 500)
        val randomIndex = (0..2).random()
        val num = arr[randomIndex]
        val dividedNum = num / 10
        return Pair(num, dividedNum)
    }


}