package my.edu.tarc.tumpangmou.ui.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentForgetPasswordBinding
import my.edu.tarc.tumpangmou.ui.index.indexFragment

class forgetPasswordFragment : Fragment() {

    private var _binding: FragmentForgetPasswordBinding? = null

    companion object {
        fun newInstance() = indexFragment()
    }

    private lateinit var viewModel: ForgetPasswordViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.forgetSendEmailButton.setOnClickListener() {
            findNavController().navigate(R.id.action_forgetPasswordFragment_to_passwordCodeFragment)
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForgetPasswordViewModel::class.java)
        // TODO: Use the ViewModel
    }

}