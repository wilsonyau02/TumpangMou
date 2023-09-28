package my.edu.tarc.tumpangmou.ui.account

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentEditProfileBinding
import my.edu.tarc.tumpangmou.databinding.FragmentLoginBinding
import my.edu.tarc.tumpangmou.ui.home.User
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL

class editProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null

    companion object {
        fun newInstance() = editProfileFragment()
    }

    private lateinit var viewModel: EditProfileViewModel

    private val binding get() = _binding!!

    private lateinit var selectedImg: Uri

    private lateinit var dialog: AlertDialog.Builder

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var storageRef: FirebaseStorage

    private val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if(uri != null){
            binding.userProfilePicture.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        dialog = AlertDialog.Builder(requireContext())
            .setMessage("Updating Profile...")
            .setCancelable(false)

        binding.userProfilePicture.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        firebaseDatabase = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")

        getFirebaseData(object: editProfileFragment.FirebaseDataCallback {
            override fun onUserEditInformationReceived(firstName: String?, lastName: String?, phoneNum: String?){
                binding.nameEditText.text = "$lastName $firstName"
                binding.phoneEditText.text = "0$phoneNum"
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
            binding.userProfilePicture.setImageBitmap(bitmap)
        }.addOnFailureListener{
            if (progressDialog.isShowing)
                progressDialog.dismiss()
            Toast.makeText(requireContext(), "Failed to retrieve the image", Toast.LENGTH_SHORT).show()
        }

//        binding.userProfilePicture.setImageResource(storageRef)


        return root
    }



    override fun onResume() {
        super.onResume()
        getFirebaseData(object: editProfileFragment.FirebaseDataCallback {
            override fun onUserEditInformationReceived(firstName: String?, lastName: String?, phoneNum: String?){
                binding.nameEditText.text = "$lastName $firstName"
                binding.phoneEditText.text = "0$phoneNum"
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (data.data != null) {
                selectedImg = data.data!!

                binding.userProfilePicture.setImageURI(selectedImg)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

        binding.profileImageEditLayout.setOnClickListener {
            getPhoto.launch("image/*")
        }


        //Edit Name
        binding.nameEditLayout.setOnClickListener() {
            navigateToEditNameFragment()
        }
        binding.editNameButton.setOnClickListener() {
            navigateToEditNameFragment()
        }

        //Edit Phone Number
        binding.phoneEditLayout.setOnClickListener() {
            navigateToEditPhoneFragment()
        }
        binding.editPhoneButton.setOnClickListener() {
            navigateToEditPhoneFragment()
        }

        //Edit Password
        binding.passwordEditLayout.setOnClickListener {
            navigateToEditPasswordFragment()
        }
        binding.editPasswordButton.setOnClickListener {
            navigateToEditPasswordFragment()
        }

        //Save Edit
        binding.saveButtonLayout.setOnClickListener() {
            //Todo: Store in database
            saveProfilePicture(binding.userProfilePicture)
            updateProfilePicture()
            findNavController().navigate(R.id.action_editProfileFragment_to_navigation_account)
        }

        //Cancel Edit
        binding.cancelButtonLayout.setOnClickListener() {
            findNavController().navigate(R.id.action_editProfileFragment_to_navigation_account)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!findNavController().navigateUp()) {
                requireActivity().onBackPressed()
            }
        }

    }

    private fun saveProfilePicture(view: View) {
        val filename = "profile.png"
        val file = File(this.context?.filesDir, filename)
        val image = view as ImageView

        val bd = image.drawable as BitmapDrawable
        val bitmap = bd.bitmap
        val outputStream: OutputStream

        try{
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            outputStream.flush()
            outputStream.close()
        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }
    }

    private fun updateProfilePicture(){

        val filename = "profile.png"
        val file = Uri.fromFile(
            File(this.context?.filesDir, filename))
        try{
            var storageRef = Firebase.storage("gs://tumpangmou.appspot.com/").reference
            val userId = sharedPrefs.getString("userId","")
//            Toast.makeText(context, userId.toString(), Toast.LENGTH_SHORT).show()
//            Toast.makeText(context, userId.toString(), Toast.LENGTH_SHORT).show()
            if (userId.isNullOrEmpty()) {
                Toast.makeText(context, "isNullOrEmpty", Toast.LENGTH_SHORT).show()
            } else {
                val profilePictureRef = storageRef.child("file").child(userId)
                profilePictureRef.putFile(file).addOnSuccessListener {
                    // Update successful.
                    Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    // Update failed.
                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun getFirebaseData(callback: editProfileFragment.FirebaseDataCallback){
        val firebaseDatabase = FirebaseDatabase.getInstance("https://tumpangmou-default-rtdb.asia-southeast1.firebasedatabase.app/")

        firebaseDatabase.getReference("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firstName = snapshot.child("firstName").getValue(String::class.java)
                    val lastName = snapshot.child("lastName").getValue(String::class.java)
                    val phoneNum = snapshot.child("phoneNum").getValue(String::class.java)

                    callback.onUserEditInformationReceived(firstName, lastName, phoneNum)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error Loading Data", Toast.LENGTH_SHORT).show()
            }
        })



    }

    //Name
    private fun navigateToEditNameFragment() {
        findNavController().navigate(R.id.action_editProfileFragment_to_editNameFragment)
    }


    //Phone
    private fun navigateToEditPhoneFragment() {
        findNavController().navigate(R.id.action_editProfileFragment_to_editPhoneNumFragment)
    }

    //Password
    private fun navigateToEditPasswordFragment() {
        findNavController().navigate(R.id.action_editProfileFragment_to_editPasswordFragment)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface FirebaseDataCallback {
        fun onUserEditInformationReceived(firstName: String?, lastName:String?, phoneNum:String?)
    }

}