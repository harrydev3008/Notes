package com.hisu.notes.ui.create_note

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hisu.notes.Constraints
import com.hisu.notes.MyApplication
import com.hisu.notes.databinding.FragmentCreateNoteBinding
import com.hisu.notes.model.Note
import com.hisu.notes.repository.NoteRepository
import com.hisu.notes.view_model.NoteViewModel
import com.hisu.notes.view_model.NoteViewModelProviderFactory

class CreateNoteFragment : Fragment() {

    private var _binding: FragmentCreateNoteBinding? = null
    private val binding get() = _binding!!
    private val navigationArgs: CreateNoteFragmentArgs by navArgs()
    private lateinit var note: Note

    private val noteViewModel: NoteViewModel by activityViewModels() {
        NoteViewModelProviderFactory(
            NoteRepository(
                requireActivity().applicationContext,
                (activity?.application as MyApplication).database.noteDAO()
            )
        )
    }

    private var resultLauncher: ActivityResultLauncher<Intent>?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noteID = navigationArgs.id

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if(result.resultCode == Activity.RESULT_OK && result.data != null) {
                noteViewModel.setNoteImage("")
                binding.noteImageContainer.visibility = View.VISIBLE
                binding.rimvNoteImage.setImageURI(result.data!!.data!!)
            }
        }

        if (noteID != -1) {
            noteViewModel.getNote(noteID.toString()).observe(this.viewLifecycleOwner) { resultNote ->
                note = resultNote
                initMoreOptionBottomSheet(note)
            }
        }

        initOnClickListener()
        binding.tvDatetime.text = noteViewModel.getDateTime()
    }

    private fun initMoreOptionBottomSheet(note: Note) = binding.apply {
        edtNoteSubTitle.setText(note.title)
        edtNoteSubTitle.setText(note.subtitle)
        edtNoteInput.setText(note.text)
        tvDatetime.text = note.dateTime

        layoutOptions.noteViewModel = noteViewModel
        layoutOptions.lifecycleOwner = viewLifecycleOwner
        layoutOptions.createNoteFragment = this@CreateNoteFragment
        layoutOptions.tvOptionsTitle.setOnClickListener { bottomSheetState() }
    }

    private fun bottomSheetState() {
        val behavior = BottomSheetBehavior.from(binding.layoutOptions.layoutContainer)
        if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resultLauncher?.launch(intent)
    }

    fun addUrl() {
        //todo: validate note b4 add or update
        //todo: add image and url
    }

    fun deleteNote() {
        noteViewModel.deleteNote(note)
        findNavController().navigateUp()
    }

    fun requestPermissionsStorage() {
        BottomSheetBehavior.from(binding.layoutOptions.layoutContainer).state = BottomSheetBehavior.STATE_COLLAPSED
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constraints.REQUEST_STORAGE_PERMISSION_CODE)
        } else {
            pickImage()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constraints.REQUEST_STORAGE_PERMISSION_CODE && grantResults.isNotEmpty()) {
            if (grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initOnClickListener() = binding.apply {
        backContainer.setOnClickListener { findNavController().popBackStack() }
        binding.ibtnSave.setOnClickListener { saveNote() }
    }

    private fun saveNote() {
        noteViewModel.setNoteColor("#333333")
        noteViewModel.setNoteImage("#333333")
        noteViewModel.setNoteURL("https://google.com")

        noteViewModel.addNewNote(
            binding.edtNoteTitle.text.toString(),
            binding.edtNoteSubTitle.text.toString(),
            binding.edtNoteInput.text.toString(),
            binding.tvDatetime.text.toString()
        )
    }

    fun setSubtitleDividerColor(color: String) {
        noteViewModel.setNoteColor(color)
        (binding.divider.background as GradientDrawable).setColor(Color.parseColor(noteViewModel.noteColor.value))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}