package com.hisu.notes.ui.create_note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hisu.notes.databinding.FragmentCreateNoteBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CreateNoteFragment : Fragment() {

    private var _binding: FragmentCreateNoteBinding? = null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addActionForBtnBack()
    }

    private fun addActionForBtnBack() = binding.backContainer.setOnClickListener {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}