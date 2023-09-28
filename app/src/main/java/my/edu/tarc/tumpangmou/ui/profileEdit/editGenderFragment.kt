package my.edu.tarc.tumpangmou.ui.profileEdit

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.NonDisposableHandle.parent
import my.edu.tarc.tumpangmou.R
import my.edu.tarc.tumpangmou.databinding.FragmentEditGenderBinding

class editGenderFragment : Fragment() {

    private var _binding: FragmentEditGenderBinding? = null


    companion object {
        fun newInstance() = editGenderFragment()
    }

    private lateinit var viewModel: EditGenderViewModel

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditGenderBinding.inflate(inflater, container, false)



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.items_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.genderAutoCompleteTextView.setAdapter(adapter)

        binding.genderAutoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
//            val selectedItem = parent.getItemAtPosition(position)
//            Toast.makeText(requireContext(), "Selected item: $selectedItem", Toast.LENGTH_SHORT).show()
        }

        //Cancel Edit
        binding.upButtonEditGender.setOnClickListener{
            findNavController().navigateUp()
        }

        //Save Edit
        binding.editGenderButton.setOnClickListener{
            
            val gender = binding.genderAutoCompleteTextView.text
            
            if (gender.isBlank()){
                binding.editGenderInput.error = "Don't Leave Blank"
            }
            else {
                //TODO: Update Database
                findNavController().navigateUp()
            }
        }

        
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditGenderViewModel::class.java)
        // TODO: Use the ViewModel
    }

}