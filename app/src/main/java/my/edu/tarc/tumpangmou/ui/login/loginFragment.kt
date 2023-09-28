package my.edu.tarc.tumpangmou.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentLoginBinding
import my.edu.tarc.tumpangmou.ui.account.accountFragment
import my.edu.tarc.tumpangmou.ui.home.User
import android.content.Context
import android.content.SharedPreferences
import androidx.activity.addCallback

import my.edu.tarc.tumpangmou.ui.index.indexFragment

class loginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var sharedPrefs: SharedPreferences

    companion object {
        fun newInstance() = loginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    private lateinit var firebaseAuth: FirebaseAuth

    private var isLoggedIn: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding.loginAccountButton.setOnClickListener {


            var inputEmail = binding.loginEmailInput.text.toString()
            var inputPassword = binding.loginPasswordInput.text.toString()

            firebaseDatabase =
                FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")

            firebaseDatabase.getReference("users").addListenerForSingleValueEvent(object :
                ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    var tempUserId: String? = null


                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        if (user?.email == inputEmail && user.password == inputPassword) {
                            tempUserId = userSnapshot.key
                            break
                        }
                    }
                    if (tempUserId != null) {
                        Toast.makeText(requireContext(), "Successful logged in", Toast.LENGTH_SHORT).show()
                        // Login successfully and go to home page
                        val editor = sharedPrefs.edit()
                        editor.putString("userId", tempUserId)

                        if (binding.rememberMeCheckBox.isChecked){
                            editor.putBoolean("isLoggedIn", true)
                        }
                        else {
                            editor.putBoolean("isLoggedIn", false)
                        }

                        editor.apply()
                        val getter = sharedPrefs.getBoolean("isLoggedIn", false)
//                        Toast.makeText(requireContext(), getter.toString(), Toast.LENGTH_SHORT).show()

//                        Toast.makeText(requireContext(), "UserID: " + tempUserId, Toast.LENGTH_SHORT).show()

                        findNavController().navigate(R.id.navigation_driver)
                    }
                    else {
                        val editor = sharedPrefs.edit()
                        editor.putBoolean("isLoggedIn", false)
                        editor.apply()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Log.e(TAG, "Error fetching user data", error.toException())
                }
            })


//            if (inputEmail.isNotEmpty() && inputPassword.isNotEmpty()) {
//
//                firebaseAuth.signInWithEmailAndPassword(inputEmail, inputPassword).addOnCompleteListener { task ->
//                    if(task.isSuccessful){
//                        startActivity(Intent(requireContext(), accountFragment::class.java))
//                        Toast.makeText(requireContext(), "successful", Toast.LENGTH_SHORT).show()
//
//                    } else {
//                        Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//            }


        }

        binding.signUpAccountClick.setOnClickListener(){
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        //Todo: Reminder not be empty

        binding.forgotPasswordButton.setOnClickListener() {

            var count = 0;

            firebaseDatabase =
                FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")

            firebaseDatabase.getReference("users").addValueEventListener(object :
                ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        count = snapshot.childrenCount.toInt()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error Loading Data", Toast.LENGTH_SHORT)
                        .show()
                }
            })

            count++
            var userId = "U" + count;

            val database = firebaseDatabase.getReference("users")

            findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        }
    }


    private fun checkLoginStatus() {
        val isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            findNavController().navigate(R.id.navigation_driver)
        }
    }

    @Override

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}