package my.edu.tarc.tumpangmou.ui.membership

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.edu.tarc.tumpangmou.databinding.FragmentMembershipMainMenuBinding

class MembershipMainMenuFragment : Fragment() {
    private var _binding: FragmentMembershipMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembershipMainMenuBinding.inflate(inflater, container, false)

        return binding.root
    }
}