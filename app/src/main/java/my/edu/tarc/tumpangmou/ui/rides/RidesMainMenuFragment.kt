package my.edu.tarc.tumpangmou.ui.rides

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import my.edu.tarc.tumpangmou.databinding.FragmentRidesMainMenuBinding

class RidesMainMenuFragment : Fragment() {
    private var _binding: FragmentRidesMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRidesMainMenuBinding.inflate(inflater, container, false)

        return binding.root
    }
}