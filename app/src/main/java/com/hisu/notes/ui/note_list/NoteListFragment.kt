package com.hisu.notes.ui.note_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.hisu.notes.R
import com.hisu.notes.databinding.FragmentNoteListBinding


private const val ARG_PARAM1 = "param1"

class NoteListFragment : Fragment() {

    private var _binding: FragmentNoteListBinding?= null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NoteListFragment().apply {
                arguments = Bundle().apply {
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
    ): View {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addActionForBtnAddNote()
    }

    private fun addActionForBtnAddNote() = binding.ibtnAddNote.setOnClickListener {
        findNavController().navigate(R.id.list_to_create)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}