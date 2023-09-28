package my.edu.tarc.tumpangmou.ui.register

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentRegisterBinding
import my.edu.tarc.tumpangmou.databinding.FragmentRegisterProfilePicBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

class registerProfilePicFragment : Fragment() {

    private var _binding: FragmentRegisterProfilePicBinding? = null

    companion object {
        fun newInstance() = registerProfilePicFragment()
    }

    private lateinit var viewModel: RegisterProfilePicViewModel

    private lateinit var selectedImg: Uri

    private lateinit var dialog: AlertDialog.Builder

    private val binding get() = _binding!!

    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String

    private val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if(uri != null){
            binding.registerProfileImage.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterProfilePicBinding.inflate(inflater, container, false)

        sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString("userId", "").toString()

//        binding.registerProfileImage.setImageResource(R.drawable.default_pic)
//        dialog = AlertDialog.Builder(requireContext())
//            .setMessage("Updating Profile...")
//            .setCancelable(false)
//
//        binding.registerProfileImage.setOnClickListener {
//            val intent = Intent()
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.type = "image/*"
//            startActivityForResult(intent, 1)
//        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (data.data != null) {
                selectedImg = data.data!!

                binding.userProfilePictureUploadBackground.visibility = View.INVISIBLE;
                binding.userProfilePictureUpload.visibility = View.INVISIBLE;

                binding.registerProfileImage.setImageURI(selectedImg)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val image = readProfilePicture()
//        if(image != null){
//            binding.registerProfileImage.setImageBitmap(image)
//        }else{
            binding.registerProfileImage.setImageResource(R.drawable.default_pic)
//        }

        binding.registerProfileImage.setOnClickListener {
            getPhoto.launch("image/*")
        }


        firebaseAuth = FirebaseAuth.getInstance()

        binding.registerAccountButton.setOnClickListener() {
            //Save profile picture to the local storage
            saveProfilePicture(binding.registerProfileImage)
//            Toast.makeText(context, "Save profile picture to the local storage", Toast.LENGTH_SHORT).show()
            //Save profile picture to the cloud storage
            uploadProfilePicture()
//            Toast.makeText(context, "//Save profile picture to the cloud storage", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_registerProfilePicFragment_to_indexFragment)


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

    private fun readProfilePicture(): Bitmap? {
        val filename = "profile.png"
        val file = File(this.context?.filesDir, filename)

        if(file.isFile){
            try{
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                return bitmap
            }catch (e: FileNotFoundException){
                e.printStackTrace()
            }
        }
        return null
    }

    private fun uploadProfilePicture(){
        val filename = "profile.png"
        val file = Uri.fromFile(
            File(this.context?.filesDir, filename))
        try{
            var storageRef = Firebase.storage("gs://tumpangmou.appspot.com/").reference
            val userId = sharedPrefs.getString("tempUserId","")
            Toast.makeText(context, "Register Successfully", Toast.LENGTH_SHORT).show()
            if(userId.isNullOrEmpty()){
                Toast.makeText(context, "isNullOrEmpty", Toast.LENGTH_SHORT).show()
            }else{
                var profilePictureRef = storageRef.child("file").child(userId)
                profilePictureRef.putFile(file)
            }

        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterProfilePicViewModel::class.java)
        // TODO: Use the ViewModel
    }

}