package my.edu.tarc.tumpangmou

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentTestingBinding
import my.edu.tarc.tumpangmou.databinding.FragmentToIndexBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [toIndexFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class toIndexFragment : Fragment() {

    private var _binding: FragmentToIndexBinding? = null
    private val binding get() = _binding!!


    companion object {
        fun newInstance() = toIndexFragment()
    }


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentToIndexBinding.inflate(inflater, container, false)

        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.buttonTesting.setOnClickListener() {
//            findNavController().navigate(R.id.action_toIndexFragment_to_indexFragment)
//        }
//
//        binding.buttonTesting2.setOnClickListener {
//            findNavController().navigate((R.id.action_toIndexFragment_to_accountFragment))
//        }
//
//    }
}