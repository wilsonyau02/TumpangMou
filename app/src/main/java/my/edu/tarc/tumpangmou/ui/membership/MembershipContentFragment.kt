package my.edu.tarc.tumpangmou.ui.membership

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentMembershipContentBinding

class MembershipContentFragment : Fragment() {
    private var _binding: FragmentMembershipContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembershipContentBinding.inflate(inflater, container, false)

        // Bronze member status
        val bronzeButton = binding.buttonBronze
        bronzeButton.setOnClickListener {
            val membershipBronzeFragment = MembershipBronzeFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewMembershipStatus, membershipBronzeFragment)
                .commit()
        }

        // Silver member status
        val silverButton = binding.buttonSilver
        silverButton.setOnClickListener {
            val membershipSilverFragment = MembershipSilverFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewMembershipStatus, membershipSilverFragment)
                .commit()
        }

        // Gold member status
        val goldButton = binding.buttonGold
        goldButton.setOnClickListener {
            val membershipGoldFragment = MembershipGoldFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewMembershipStatus, membershipGoldFragment)
                .commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonBackToMembershipMain.setOnClickListener {
            findNavController().navigate(R.id.action_membershipContentFragment_to_membershipMainFragment)
        }
    }
}