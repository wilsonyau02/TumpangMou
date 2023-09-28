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
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentEditPhoneNumBinding

class editPhoneNumFragment : Fragment() {

    private var _binding: FragmentEditPhoneNumBinding? = null

    companion object {
        fun newInstance() = editPhoneNumFragment()
    }

    private lateinit var viewModel: EditPhoneNumViewModel

    private val binding get() = _binding!!

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditPhoneNumBinding.inflate(inflater, container, false)

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        firebaseDatabase = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")

        return binding.root
    }

    var phone: Boolean  = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        //Cancel Edit
        binding.upButtonEditPhone.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.editPhoneInputText.doOnTextChanged { text, start, before, count ->
            if (text!!.length > 10 && text!!.isNotBlank()){
                binding.editPhoneInput.error = "Invalid Phone Format!"
                binding.editPhoneInput.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                phone = false
            }
            else if (text!!.isBlank()){
                binding.editPhoneInput.setEndIconDrawable(null)
                binding.editPhoneInput.error = null
                phone = false
            }
            else {
                binding.editPhoneInput.error = null
                binding.editPhoneInput.errorIconDrawable = null
                binding.editPhoneInput.endIconMode = TextInputLayout.END_ICON_CUSTOM
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.sharp_check_circle_24)
                binding.editPhoneInput.setEndIconDrawable(drawable)
                phone = false

            }

        }

        //Save Edit
        binding.editPhoneNumberButton.setOnClickListener{
            if (binding.editPhoneInputText.text.toString().length == 9 ||  binding.editPhoneInputText.text.toString().length == 10) {
                phone = true
            }
            else {
                binding.editPhoneInput.error = "Invalid Format!"
                phone = false
            }

            if(phone) {

                val updatedPhoneNumber = binding.editPhoneInputText.text.toString()
                firebaseDatabase.getReference("users").child(userId).child("phoneNum").setValue(updatedPhoneNumber)
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

                findNavController().navigate(R.id.action_editPhoneNumFragment_to_editProfileFragment)
            }

            //TODO: save to database & go back to profile edit page
//            findNavContro
        //            }|"ller().navigate(R.id.'''}?)
            
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditPhoneNumViewModel::class.java)
        // TODO: Use the ViewModel
    }

}