package my.edu.tarc.tumpangmou.ui.membership

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentMembershipMainBinding

class MembershipMainFragment : Fragment() {
    private var _binding: FragmentMembershipMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var cumulativePointsTextView: TextView
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var postId:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembershipMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonToMembershipStatus.setOnClickListener {
            findNavController().navigate(R.id.action_membershipMainFragment_to_membershipContentFragment)
        }

        binding.buttonBackToAccount.setOnClickListener {
            findNavController().navigate(R.id.action_membershipMainFragment_to_navigation_account)
        }
    }
}