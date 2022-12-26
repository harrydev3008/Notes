package com.hisu.notes.ui.note_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.hisu.notes.MyApplication
import com.hisu.notes.R
import com.hisu.notes.databinding.FragmentNoteListBinding
import com.hisu.notes.repository.NoteRepository
import com.hisu.notes.view_model.NoteViewModel
import com.hisu.notes.view_model.NoteViewModelProviderFactory

class NoteListFragment : Fragment() {

    private var _binding: FragmentNoteListBinding?= null
    private val binding get() = _binding!!
    private lateinit var noteAdapter: NoteAdapter

    private val noteViewModel: NoteViewModel by activityViewModels() {
        NoteViewModelProviderFactory(
            NoteRepository(
                requireActivity().applicationContext,
                (activity?.application as MyApplication).database.noteDAO()
            )
        )
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

        noteAdapter = NoteAdapter {
            val action = NoteListFragmentDirections.listToCreate(it.id)
            findNavController().navigate(action)
        }

        binding.rvNotes.apply {
            adapter = noteAdapter
        }

        noteViewModel.getAllNotes().observe(this.viewLifecycleOwner) {
            val res = it

            res?.let {
                noteAdapter.notes = res
            }
        }

        binding.edtSearchNote.addTextChangedListener {
            noteViewModel.searchNote(it.toString()).observe(this.viewLifecycleOwner) { notes ->
                notes?.let {
                    noteAdapter.notes = notes
                }
            }
        }
    }

    private fun addActionForBtnAddNote() = binding.ibtnAddNote.setOnClickListener {
        val action = NoteListFragmentDirections.listToCreate(-1)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}