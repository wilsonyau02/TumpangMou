package my.edu.tarc.tumpangmou.ui.profileEdit

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentEditPasswordBinding
import java.util.regex.Pattern

class editPasswordFragment : Fragment() {

     private var _binding: FragmentEditPasswordBinding? = null

     companion object {
        fun newInstance() = editPasswordFragment()
    }

    private lateinit var viewModel: EditPasswordViewModel

    private val binding get() = _binding!!

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditPasswordBinding.inflate(inflater, container, false)

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        firebaseDatabase = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")

        return binding.root
    }

    var editPasswordValidated: Boolean  = false
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        var passwordInput = binding.editPasswordInputText
        var confirmPasswordInput = binding.editConfirmPasswordInputText


        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null
        val DELAY_TIME = 100L // 1 second delay

        confirmPasswordInput.doOnTextChanged { text, start, before, count ->
            runnable?.let { handler.removeCallbacks(it) } // Remove the previous callback if any
            runnable = Runnable {
                if (passwordInput.text.toString() != confirmPasswordInput.text.toString()) {
                    binding.editConfirmPasswordInput.error = "Does not match with password entered!"
                    editPasswordValidated = false
                }
                else {
                    binding.editConfirmPasswordInput.error = null
                    editPasswordValidated = true
                }
            }
            handler.postDelayed(runnable!!, DELAY_TIME)
        }

        passwordInput.doOnTextChanged { text, start, before, count ->
            runnable?.let { handler.removeCallbacks(it) } // Remove the previous callback if any

            var password = binding.editPasswordInputText.text.toString()
            val hasUppercase = password.matches("(?=.*[A-Z]).+".toRegex())
            val hasLowercase = password.matches("(?=.*[a-z]).+".toRegex())
            val passwordPattern = Pattern.compile("[^A-Za-z0-9]")
            val hasSpecialCharacter = passwordPattern.matcher(password).find()

            var validPassword: Boolean = false

            runnable = Runnable {
                if (password.length < 8) {
                    binding.editPasswordInput.error = "Please include at least 8 characters."
                    validPassword = false
                }
                else if (!password.matches(Regex(".*\\d.*"))){
                    binding.editPasswordInput.error = "Please include at least 1 number."
                    validPassword = false
                }
                else if (!hasUppercase || !hasLowercase) {
                    binding.editPasswordInput.error = "Please include at least 1 UPPERCASE & 1 lowercase."
                    validPassword = false
                }
                else if (!hasSpecialCharacter) {
                    binding.editPasswordInput.error = "Please include at least 1 special character !,*,@..."
                    validPassword = false
                }
                else {
                    binding.editPasswordInput.error = null
                    validPassword = true
                }
            }
            handler.postDelayed(runnable!!, DELAY_TIME)

            editPasswordValidated = validPassword
        }

        //Save Edit
        binding.editPasswordButton.setOnClickListener{
            

            if(editPasswordValidated){
                //TODO: Update Database
                val updatedPassword = binding.editPasswordInputText.text.toString()
                firebaseDatabase.getReference("users").child(userId).child("password").setValue(updatedPassword)
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

                findNavController().navigate(R.id.action_editPasswordFragment_to_editProfileFragment)
                Toast.makeText(requireContext(), "Password Valid", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireContext(), "Password Invalid", Toast.LENGTH_SHORT).show()

            }
            
        }

        //Cancel Edit
        binding.upButtonEditPassword.setOnClickListener{
            //TODO: save to database & go back to profile edit page
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditPasswordViewModel::class.java)
        // TODO: Use the ViewModel
    }

}