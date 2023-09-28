package my.edu.tarc.tumpangmou.ui.membership

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import my.edu.tarc.tumpangmou.databinding.FragmentMembershipGoldBinding

class MembershipGoldFragment : Fragment() {
    private var _binding: FragmentMembershipGoldBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembershipGoldBinding.inflate(inflater, container, false)

        return binding.root
    }
}