package my.edu.tarc.tumpangmou.ui.rides

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my.edu.tarc.tumpangmou.databinding.FragmentLoadPostPopUpWindowBinding
import java.util.Calendar

class LoadPostPopUpWindowFragment : DialogFragment() {
    private var _binding: FragmentLoadPostPopUpWindowBinding? = null
    private val binding get() = _binding!!
    lateinit var listener: OnFragmentInteractionListener
    private var filteredRidesParcelable: List<Parcelable> = arrayListOf<Parcelable>()
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadPostPopUpWindowBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSearchPost.setOnClickListener {
            // Prompt error if input fields are empty and restrict the user from proceeding
            if (binding.spinnerSearchDepartureInner.selectedItem.toString() == "Select your travelling place"
                || binding.spinnerSearchDestinationInner.selectedItem.toString() == "Select your departure place"
                || binding.editTextDateInner.text.toString().isEmpty()) {
                val toast = Toast.makeText(requireContext(), "Please fill in the necessary details", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM, 0, 250)
                toast.show()
                return@setOnClickListener
            } else {
                isSearching = true

                CoroutineScope(Dispatchers.Default).launch {
                    searchRides()

                    withContext(Dispatchers.Main) {
                        isSearching = false
                    }
                }

                if (isSearching) {
                    CoroutineScope(Dispatchers.Main).launch {
                        while (isSearching) {
                            delay(100)
                        }
                        dismiss()
                    }
                } else {
                    dismiss()
                }
            }
        }

        binding.editTextDateInner.setOnClickListener {
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

                    binding.editTextDateInner.setText(selectedDate)
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
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        // Pass the filteredRides to the fragment where they will be displayed
        val bundle = Bundle()
        bundle.putParcelableArrayList("rides", ArrayList(filteredRidesParcelable))

        listener.onBundlePass(bundle)
    }

    private suspend fun searchRides() {
        val databaseRef = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("driver_post")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Parse the ride data from Firebase and store it in a list
                val rideList = arrayListOf<RidesPost>()
                for (postSnapshot in snapshot.children) {
                    val ride = postSnapshot.children.first().getValue(RidesPost::class.java)
                    rideList.add(ride!!)
                }

                // Filter the rideList based on the source and destination selected by the user
                val filteredRidesPlace = rideList.filter { it.departurePlace == binding.spinnerSearchDepartureInner.selectedItem.toString()
                        && it.travellingPlace == binding.spinnerSearchDestinationInner.selectedItem.toString()
                        && it.travelDate == binding.editTextDateInner.text.toString()}

                // Remove completed rides from the list
                val filteredRides = filteredRidesPlace.filter { it.status != "Completed" }

                // Convert the list of RidesPost objects to a list of Parcelable objects
                filteredRidesParcelable = filteredRides.map { it as Parcelable }.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    public interface OnFragmentInteractionListener {
        fun onBundlePass(bundle: Bundle)
    }
}

