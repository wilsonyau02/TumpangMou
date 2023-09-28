package my.edu.tarc.tumpangmou.ui.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentIndexBinding
import my.edu.tarc.tumpangmou.databinding.FragmentPasswordCodeBinding

class passwordCodeFragment : Fragment() {

    private var _binding: FragmentPasswordCodeBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = passwordCodeFragment()
    }

    private lateinit var viewModel: PasswordCodeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPasswordCodeBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        binding.forgetCodeInput.doOnTextChanged { text, start, before, count ->
            if (text!!.length > 6) {
                binding.forgetCodeInput.error = "6 digit only!"
            } else if (text.length < 6) {
                binding.forgetCodeInput.error = null
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.forgetSendEmailButton.setOnClickListener() {
            findNavController().navigate(R.id.action_passwordCodeFragment_to_loginFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PasswordCodeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}