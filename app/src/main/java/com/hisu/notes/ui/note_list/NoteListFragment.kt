package com.hisu.notes.ui.note_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hisu.notes.MyApplication
import com.hisu.notes.databinding.FragmentNoteListBinding
import com.hisu.notes.repository.NoteRepository
import com.hisu.notes.view_model.NoteViewModel
import com.hisu.notes.view_model.NoteViewModelProviderFactory

class NoteListFragment : Fragment() {

    private var _binding: FragmentNoteListBinding? = null
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

        setUpNoteRecyclerView()
        addActionForBtnAddNote()
        addActionForEdtSearch()
        getNotes()
    }

    private fun setUpNoteRecyclerView() = binding.rvNotes.apply {
        noteAdapter = NoteAdapter { note ->
            val action = NoteListFragmentDirections.listToCreate(note.id)
            findNavController().navigate(action)
        }

        adapter = noteAdapter
    }

    private fun addActionForEdtSearch() = binding.edtSearchNote.addTextChangedListener {
        if (it != null && it.isNotEmpty())
            noteViewModel.searchNote(it.toString()).observe(this.viewLifecycleOwner) { notes ->
                notes?.let { noteAdapter.notes = notes }
            }
    }

    private fun addActionForBtnAddNote() = binding.ibtnAddNote.setOnClickListener {
        val action = NoteListFragmentDirections.listToCreate(-1)
        findNavController().navigate(action)
    }

    private fun getNotes() = viewLifecycleOwner.lifecycleScope.launchWhenCreated {
        noteViewModel.getAllNotes().observe(this@NoteListFragment.viewLifecycleOwner) { notes ->
            notes?.let { noteAdapter.notes = it }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}