package my.edu.tarc.tumpangmou.ui.account

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentAccountBinding
import my.edu.tarc.tumpangmou.ui.home.DriverPost
import my.edu.tarc.tumpangmou.ui.home.HomeFragment
import my.edu.tarc.tumpangmou.ui.home.User
import java.io.File

class accountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null

    companion object {
        fun newInstance() = accountFragment()
    }


    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var viewModel: AccountViewModel

    private val binding get() = _binding!!

    private lateinit var bottomNavView: BottomNavigationView

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        firebaseDatabase = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")


        getFirebaseData(object: FirebaseDataCallback {
            override fun onAccountNameReceived(firstName: String?, lastName: String?, gender: String?){
                var title:String = ""
                if (gender == "Male"){
                    title = "Mr."
                }
                else{
                    title = "Mrs./Ms."
                }
                binding.accountName.text = "$title $lastName $firstName"
            }

        })

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Fetching...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val storageRef = Firebase.storage("gs://tumpangmou.appspot.com/").reference

        val image = storageRef.child("file").child(userId)

        val localFile = File.createTempFile("tempImage", "png")
        image.getFile(localFile).addOnSuccessListener {

            if (progressDialog.isShowing)
                progressDialog.dismiss()

            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.imageView.setImageBitmap(bitmap)
        }.addOnFailureListener{if (progressDialog.isShowing)
            progressDialog.dismiss()
            Toast.makeText(requireContext(), "Failed to retrieve the image", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        getFirebaseData(object: FirebaseDataCallback {
            override fun onAccountNameReceived(firstName: String?, lastName: String?, gender: String?){
                var title:String = ""
                if (gender == "Male"){
                    title = "Mr."
                }
                else{
                    title = "Mrs./Ms."
                }
                binding.accountName.text = "$title $lastName $firstName"
            }

        })



    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        binding.accountEditProfile.setOnClickListener() {
            findNavController().navigate(R.id.action_accountFragment_to_editProfileFragment)
        }

        binding.accountMembership.setOnClickListener() {
            findNavController().navigate(R.id.action_accountFragment_to_membershipMainFragment)
        }

        bottomNavView = binding.navView

        val navController = findNavController()
        bottomNavView.setupWithNavController(navController)

        // Set the OnClickListener for the logout button
        binding.accountLogout.setOnClickListener {
            val editor = sharedPrefs.edit()
            editor.putString("userId", null)
            editor.putBoolean("isLoggedIn", false)
            editor.apply()
//            val getter = sharedPrefs.getBoolean("isLoggedIn", false)
//            Toast.makeText(requireContext(), getter.toString(), Toast.LENGTH_SHORT).show()

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.indexFragment, true)
                .build()
            navController.navigate(R.id.action_accountFragment_to_indexFragment, null, navOptions)


        }

        binding.accountSurveyForm.setOnClickListener {
            val url = "https://forms.gle/XgCtz4GveJCPfBi16"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val storageRef = Firebase.storage("gs://tumpangmou.appspot.com/").reference

        val image = storageRef.child("file").child(userId)

        val localFile = File.createTempFile("tempImage", "png")
        image.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.imageView.setImageBitmap(bitmap)
        }.addOnFailureListener{
//            Toast.makeText(requireContext(), "Failed to retrieve the image", Toast.LENGTH_SHORT).show()
        }

    }


    private fun getFirebaseData(callback: accountFragment.FirebaseDataCallback){
        val firebaseDatabase = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")

        firebaseDatabase.getReference("users").child("$userId").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firstName = snapshot.child("firstName").getValue(String::class.java)
                    val lastName = snapshot.child("lastName").getValue(String::class.java)
                    val gender = snapshot.child("gender").getValue(String::class.java)



                    callback.onAccountNameReceived(firstName, lastName, gender)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error Loading Data", Toast.LENGTH_SHORT).show()
            }
        })

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface FirebaseDataCallback {
        fun onAccountNameReceived(firstName: String?, lastName:String?, gender:String?)
    }

}