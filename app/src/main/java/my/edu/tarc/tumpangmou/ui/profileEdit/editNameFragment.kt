package my.edu.tarc.tumpangmou.ui.profileEdit

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentEditNameBinding
import my.edu.tarc.tumpangmou.ui.account.editProfileFragment
import my.edu.tarc.tumpangmou.ui.home.User
import my.edu.tarc.tumpangmou.ui.home.userViewModel

class editNameFragment : Fragment() {

    private var _binding: FragmentEditNameBinding? = null

    companion object {
        fun newInstance() = editNameFragment()
    }

    private lateinit var viewModel: EditNameViewModel

    private val binding get() = _binding!!

    val userViewModel : userViewModel by activityViewModels()

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var postId:String

//    val userViewModel : ContactViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditNameBinding.inflate(inflater, container, false)

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        firebaseDatabase = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    var firstName: Boolean = false
    var lastName: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        //Cancel Edit
        binding.upButtonEditName.setOnClickListener {
            findNavController().navigateUp()
        }

        //Check First Name
        binding.editFirstNameInputText.doOnTextChanged { text, start, before, count ->
            if (text!!.length > 20 && text!!.isNotBlank()) {
                binding.editFirstNameInput.error =
                    "We're sorry, but the name you entered is too long. \nPlease try again with a shorter name."
                binding.editFirstNameInput.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                firstName = false
            } else if (text!!.isBlank()) {
                binding.editFirstNameInput.setEndIconDrawable(null)
                binding.editFirstNameInput.error = null
                firstName = false
            } else {
                binding.editFirstNameInput.error = null
                binding.editFirstNameInput.errorIconDrawable = null
                binding.editFirstNameInput.endIconMode = TextInputLayout.END_ICON_CUSTOM
                val drawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.sharp_check_circle_24)
                binding.editFirstNameInput.setEndIconDrawable(drawable)
                firstName = false
            }
        }

        //Check Last Name
        binding.editLastNameInputText.doOnTextChanged { text, start, before, count ->
            if (text!!.length > 20 && text!!.isNotBlank()) {
                binding.editLastNameInput.error =
                    "We're sorry, but the name you entered is too long. \nPlease try again with a shorter name."
                binding.editLastNameInput.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                lastName = false
            } else if (text!!.isBlank()) {
                binding.editLastNameInput.setEndIconDrawable(null)
                binding.editLastNameInput.error = null
                lastName = false
            } else {
                binding.editLastNameInput.error = null
                binding.editLastNameInput.errorIconDrawable = null
                binding.editLastNameInput.endIconMode = TextInputLayout.END_ICON_CUSTOM
                val drawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.sharp_check_circle_24)
                binding.editLastNameInput.setEndIconDrawable(drawable)
                lastName = false
            }
        }

        //Save Edit
        binding.editNameButton.setOnClickListener {
            if (binding.editFirstNameInputText.text?.isNotBlank() == true && binding.editLastNameInputText.text?.isNotBlank() == true) {
                firstName = true
                lastName = true
            } else {
                if (binding.editFirstNameInputText.text?.isNotBlank() == false) {
                    binding.editFirstNameInput.error = "Don't Leave Blank"
                } else {
                    binding.editLastNameInput.error = "Don't Leave Blank"
                }
                firstName = false
                lastName = false
            }

            if (firstName && lastName) {
//                TODO: Update Database

                val firstName = binding.editFirstNameInputText.text.toString()
                val lastName = binding.editLastNameInputText.text.toString()

                firebaseDatabase.getReference("users").child(userId).child("firstName").setValue(firstName)
                firebaseDatabase.getReference("users").child(userId).child("lastName").setValue(lastName)
                    .addOnSuccessListener {
                        // The data has been successfully updated
                        // Do something here, like showing a Toast message
                        Toast.makeText(requireContext(), "Data updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        // The update operation has failed
                        // Do something here, like showing an error message
                        Toast.makeText(requireContext(), "Failed to update data", Toast.LENGTH_SHORT).show()
                    }

                findNavController().navigate(R.id.action_editNameFragment_to_editProfileFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditNameViewModel::class.java)
        // TODO: Use the ViewModel
    }

}