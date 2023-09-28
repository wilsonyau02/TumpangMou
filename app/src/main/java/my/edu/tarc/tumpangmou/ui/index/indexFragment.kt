package my.edu.tarc.tumpangmou.ui.index

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentIndexBinding

class indexFragment : Fragment() {

    private var _binding: FragmentIndexBinding? = null

    companion object {
        fun newInstance() = indexFragment()
    }

    private lateinit var viewModel: IndexViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentIndexBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUpButton.setOnClickListener() {
            findNavController().navigate(R.id.action_indexFragment_to_registerFragment)
        }

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_indexFragment_to_loginFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(IndexViewModel::class.java)
        // TODO: Use the ViewModel
    }


}