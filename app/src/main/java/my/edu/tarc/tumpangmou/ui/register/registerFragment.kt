package my.edu.tarc.tumpangmou.ui.register

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.set
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentRegisterBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import my.edu.tarc.tumpangmou.ui.login.loginFragment
import java.util.*
import java.util.regex.Pattern

class registerFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    companion object {
        fun newInstance() = registerFragment()
    }

    private lateinit var viewModel: RegisterViewModel

    private val binding get() = _binding!!

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var sharedPrefs: SharedPreferences

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(requireContext())

        var auth: FirebaseAuth = Firebase.auth
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    var email: Boolean  = false
    var phone: Boolean  = false
    var firstName: Boolean  = false
    var lastName: Boolean  = false
    var gender: Boolean  = false
    var dateOfBirth: Boolean  = false
    var passwordValidated: Boolean  = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]


        var emailInput = binding.registerEmailInputText
        var phoneInput = binding.registerPhoneInputText
        var firstNameInput = binding.registerFirstNameInputText
        var lastNameInput = binding.registerLastNameInputText
        var dobInput = binding.registerBODInputText
        var genderGroupInput = binding.registerGender
        var maleInput = binding.buttonMale
        var femaleInput = binding.buttonFemale
        var passwordInput = binding.registerPasswordInputText
        var confirmPasswordInput = binding.registerConfirmPasswordInputText

        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null
        val DELAY_TIME = 100L // 1 second delay


        binding.registerEmailInputText.doOnTextChanged { text, start, before, count ->
            if (!Patterns.EMAIL_ADDRESS.matcher(text).matches() && text!!.isNotBlank()){
                binding.registerEmailInput.error = "Invalid Email Format!"
                binding.registerEmailInput.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                email = false
            }
            else if (text!!.isBlank()){
                binding.registerEmailInput.setEndIconDrawable(null)
                binding.registerEmailInput.error = null
                email = false
            }
            else {
                binding.registerEmailInput.error = null
                binding.registerEmailInput.errorIconDrawable = null
                binding.registerEmailInput.endIconMode = TextInputLayout.END_ICON_CUSTOM
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.sharp_check_circle_24)
                binding.registerEmailInput.setEndIconDrawable(drawable)
                email = false
            }
        }

        binding.registerPhoneInputText.doOnTextChanged { text, start, before, count ->
            if (text!!.length > 10 && text!!.isNotBlank()){
                binding.registerPhoneInput.error = "Invalid Phone Format!"
                binding.registerPhoneInput.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                phone = false
            }
            else if (text!!.isBlank()){
                binding.registerPhoneInput.setEndIconDrawable(null)
                binding.registerPhoneInput.error = null
                phone = false
            }
            else {
                binding.registerPhoneInput.error = null
                binding.registerPhoneInput.errorIconDrawable = null
                binding.registerPhoneInput.endIconMode = TextInputLayout.END_ICON_CUSTOM
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.sharp_check_circle_24)
                binding.registerPhoneInput.setEndIconDrawable(drawable)
                phone = false

            }

        }

        binding.registerFirstNameInputText.doOnTextChanged { text, start, before, count ->
            if (text!!.length > 20 && text!!.isNotBlank()){
                binding.registerFirstNameInput.error = "We're sorry, but the name you entered is too long. \nPlease try again with a shorter name."
                binding.registerFirstNameInput.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                firstName = false
            }
            else if (text!!.isBlank()){
                binding.registerFirstNameInput.setEndIconDrawable(null)
                binding.registerFirstNameInput.error = null
                firstName = false
            }
            else {
                binding.registerFirstNameInput.error = null
                binding.registerFirstNameInput.errorIconDrawable = null
                binding.registerFirstNameInput.endIconMode = TextInputLayout.END_ICON_CUSTOM
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.sharp_check_circle_24)
                binding.registerFirstNameInput.setEndIconDrawable(drawable)
                firstName = false
            }
        }

        binding.registerLastNameInputText.doOnTextChanged { text, start, before, count ->
            if (text!!.length > 20 && text!!.isNotBlank()){
                binding.registerLastNameInput.error = "We're sorry, but the name you entered is too long. \nPlease try again with a shorter name."
                binding.registerLastNameInput.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                lastName = false
            }
            else if (text!!.isBlank()){
                binding.registerLastNameInput.setEndIconDrawable(null)
                binding.registerLastNameInput.error = null
                lastName = false
            }
            else {
                binding.registerLastNameInput.error = null
                binding.registerLastNameInput.errorIconDrawable = null
                binding.registerLastNameInput.endIconMode = TextInputLayout.END_ICON_CUSTOM
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.sharp_check_circle_24)
                binding.registerLastNameInput.setEndIconDrawable(drawable)
                lastName = false
            }
        }

        binding.registerBODInputText.setOnClickListener() {

            // create a calendar instance with the current date as the default
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            // create a date picker dialog with the current date as the default
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // set the selected date to the input field
                    val selectedDate = "$dayOfMonth/${month + 1}/$year"
                    binding.registerBODInputText?.setText(selectedDate)

                    // store the selected date in a variable
                    viewModel.selectedDate = selectedDate
                },
                year,
                month,
                dayOfMonth
            )

            // show the date picker dialog to the user
            datePickerDialog.show()

            //TODO: GET THE DATE AND STORE
        }

        confirmPasswordInput.doOnTextChanged { text, start, before, count ->
            runnable?.let { handler.removeCallbacks(it) } // Remove the previous callback if any
            runnable = Runnable {
                if (passwordInput.text.toString() != confirmPasswordInput.text.toString()) {
                    binding.registerConfirmPasswordInput.error = "Does not match with password entered!"
                    passwordValidated = false
                }
                else {
                    binding.registerConfirmPasswordInput.error = null
                    passwordValidated = true
                }
            }
            handler.postDelayed(runnable!!, DELAY_TIME)
        }

        passwordInput.doOnTextChanged { text, start, before, count ->
            runnable?.let { handler.removeCallbacks(it) } // Remove the previous callback if any

            var password = binding.registerPasswordInputText.text.toString()
            val hasUppercase = password.matches("(?=.*[A-Z]).+".toRegex())
            val hasLowercase = password.matches("(?=.*[a-z]).+".toRegex())
            val passwordPattern = Pattern.compile("[^A-Za-z0-9]")
            val hasSpecialCharacter = passwordPattern.matcher(password).find()

            var validPassword: Boolean = false

            runnable = Runnable {
                if (password.length < 8) {
                    binding.registerPasswordInput.error = "Please include at least 8 characters."
                    validPassword = false
                }
                else if (!password.matches(Regex(".*\\d.*"))){
                    binding.registerPasswordInput.error = "Please include at least 1 number."
                    validPassword = false
                }
                else if (!hasUppercase || !hasLowercase) {
                    binding.registerPasswordInput.error = "Please include at least 1 UPPERCASE & 1 lowercase."
                    validPassword = false
                }
                else if (!hasSpecialCharacter) {
                    binding.registerPasswordInput.error = "Please include at least 1 special character !,*,@..."
                    validPassword = false
                }
                else {
                    binding.registerPasswordInput.error = null
                    validPassword = true
                }
            }
            handler.postDelayed(runnable!!, DELAY_TIME)

            passwordValidated = validPassword
        }




        binding.registerProfileButton.setOnClickListener() {

            if (binding.registerPhoneInputText.text.toString().length == 9 ||  binding.registerPhoneInputText.text.toString().length == 10) {
                phone = true
            }
            else {
                binding.registerPhoneInput.error = "Invalid Format!"
                phone = false
            }

            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

            if (emailPattern.matches(binding.registerEmailInputText.text.toString())) {
                email = true
            }
            else {
                binding.registerEmailInput.error = "Invalid Format!"
                phone = false
            }

            if (binding.registerFirstNameInputText.text?.isNotBlank() == true && binding.registerLastNameInputText.text?.isNotBlank() == true) {
                firstName = true
                lastName = true
            } else {
                if (binding.registerFirstNameInputText.text?.isNotBlank() == false) {
                    binding.registerFirstNameInput.error = "Don't Leave Blank"
                }
                else {
                    binding.registerLastNameInput.error = "Don't Leave Blank"
                }
                firstName = false
                lastName = false
            }

            if (!binding.buttonMale.isChecked && !binding.buttonFemale.isChecked) {
                Toast.makeText(context, "Please Choose Your Gender", Toast.LENGTH_SHORT).show()
                gender = false
            }
            else {
                gender = true
            }

            dateOfBirth = binding.registerBODInputText.text?.isBlank() != true

            if (email && phone && firstName && lastName && dateOfBirth && gender && passwordValidated) {
                //Upload data to firebase
                uploadData()

                Toast.makeText(context, "Let's upload profile image", Toast.LENGTH_SHORT).show()

                findNavController().navigate(R.id.action_registerFragment_to_registerProfilePicFragment)
            }

        }

    }

    private fun uploadData(){
        var email = binding.registerEmailInputText.text.toString()
        var phone = binding.registerPhoneInputText.text.toString()
        var firstName = binding.registerFirstNameInputText.text.toString()
        var lastName = binding.registerLastNameInputText.text.toString()
        var dob = binding.registerBODInputText.text.toString()
        var maleButton = binding.buttonMale
        var password = binding.registerPasswordInputText.text.toString()
        var gender:String

        if (maleButton.isChecked()){
            gender = "Male"
        }else{
            gender = "Female"
        }

        firebaseDatabase = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")
        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        firebaseDatabase.getReference("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userCount = snapshot.childrenCount
                Toast.makeText(requireContext(), "Number of users: $userCount", Toast.LENGTH_SHORT).show()
                Log.d("Testing", userCount.toString())
                val count = userCount.toInt()
                val userId = "U${count + 1}"

                val database = firebaseDatabase.getReference("users")

                database.child(userId).setValue(
                    mapOf(
                        "userID" to userId,
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "email" to email,
                        "phoneNum" to phone,
                        "dob" to dob,
                        "gender" to gender,
                        "password" to password
                    )
                )

                val editor = sharedPrefs.edit()
                editor.putString("tempUserId", userId)
                editor.apply()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })


    }


}