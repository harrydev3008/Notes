package com.hisu.notes.ui.create_note

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
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
import com.gdacciaro.iOSDialog.iOSDialogBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hisu.notes.utils.Constraints
import com.hisu.notes.MyApplication
import com.hisu.notes.R
import com.hisu.notes.utils.RealPathUtil
import com.hisu.notes.databinding.FragmentCreateNoteBinding
import com.hisu.notes.model.Note
import com.hisu.notes.repository.NoteRepository
import com.hisu.notes.ui.dialog.AddUrlDialog
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

    private var resultLauncher: ActivityResultLauncher<Intent>? = null

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

        initPickImageResultLauncher()
        initOnClickListener()
        initMoreOptionsBottomSheet()

        if (noteID != -1) {
            noteViewModel.getNote(noteID.toString())
                .observe(this.viewLifecycleOwner) { resultNote ->
                    note = resultNote
                    bindNote(note)
                }
        } else {
            binding.apply {
                tvDatetime.text = noteViewModel.getDateTime()
                (divider.background as GradientDrawable).setColor(Color.parseColor("#333333"))
                binding.ibtnSave.setOnClickListener { saveNote() }
            }
        }
    }

    private fun initPickImageResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val uri = result.data!!.data!!
                    binding.noteImageContainer.visibility = View.VISIBLE
                    binding.rimvNoteImage.setImageURI(uri)
                    noteViewModel.setNoteImage(RealPathUtil.getRealPath(requireContext(), uri))
                }
            }
    }

    private fun bindNote(note: Note) = binding.apply {
        edtNoteTitle.setText(note.title)
        edtNoteSubTitle.setText(note.subtitle)
        edtNoteInput.setText(note.text)

        note.url?.let {
            noteUrlContainer.visibility = View.VISIBLE
            tvUrl.text = it
            noteViewModel.setNoteURL(it)
        }

        tvDatetime.text = note.dateTime
        note.color?.let {
            (divider.background as GradientDrawable).setColor(Color.parseColor(it))
            noteViewModel.setNoteColor(it)
        }

        note.imagePath?.let {
            rimvNoteImage.setImageBitmap(BitmapFactory.decodeFile(it))
            noteImageContainer.visibility = View.VISIBLE
            noteViewModel.setNoteImage(it)
        }

        ibtnSave.setOnClickListener { updateNote() }
        ibtnRemoveImage.setOnClickListener { removeNoteImage() }
        ibtnRemoveUrl.setOnClickListener { removeNoteUrl() }
    }

    private fun removeNoteImage() {
        noteViewModel.setNoteImage(null)
        binding.rimvNoteImage.setImageBitmap(null)
        binding.noteImageContainer.visibility = View.GONE
    }

    private fun removeNoteUrl() {
        noteViewModel.setNoteURL(null)
        binding.tvUrl.text = ""
        binding.noteUrlContainer.visibility = View.GONE
    }

    private fun initMoreOptionsBottomSheet() = binding.layoutOptions.apply {
        noteViewModel = noteViewModel
        lifecycleOwner = viewLifecycleOwner
        createNoteFragment = this@CreateNoteFragment
        tvOptionsTitle.setOnClickListener { bottomSheetState() }
    }

    private fun bottomSheetState() {
        val behavior = BottomSheetBehavior.from(binding.layoutOptions.layoutContainer)
        if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resultLauncher?.launch(intent)
    }

    fun addUrl() {
        val dialog = AddUrlDialog(requireContext(), Gravity.CENTER)
        dialog.addUrlEvent {
            val url = dialog.getUrl()
            if (isValidURL(url)) {
                BottomSheetBehavior.from(binding.layoutOptions.layoutContainer).state =
                    BottomSheetBehavior.STATE_COLLAPSED
                noteViewModel.setNoteURL(url)
                binding.noteUrlContainer.visibility = View.VISIBLE
                binding.tvUrl.text = noteViewModel.url.value
                dialog.dismissDialog()
            }
        }
        dialog.showDialog()
    }

    private fun isValidURL(url: String): Boolean {
        if (url.isBlank() || url.isEmpty()) {
            Toast.makeText(
                context,
                requireContext().getString(R.string.empty_url_err),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (!Patterns.WEB_URL.matcher(url).matches()) {
            Toast.makeText(
                context,
                requireContext().getString(R.string.invalid_url_err),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    fun deleteNote() {
        iOSDialogBuilder(activity)
            .setTitle(activity?.getString(R.string.delete_note))
            .setSubtitle(activity?.getString(R.string.delete_note_confirm))
            .setNegativeListener(activity?.getString(R.string.no)) { it.dismiss() }
            .setPositiveListener(activity?.getString(R.string.yes)) {
                it.dismiss()
                noteViewModel.deleteNote(note)
                findNavController().navigateUp()
            }
            .setBoldPositiveLabel(true)
            .build().show()
    }

    private fun isValidNoteData(): Boolean {
        if (binding.edtNoteTitle.text.toString().isBlank()) {
            Toast.makeText(
                context,
                requireContext().getString(R.string.empty_note_title_err),
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (binding.edtNoteSubTitle.text.toString()
                .isBlank() && binding.edtNoteInput.text.toString().isBlank()
        ) {
            Toast.makeText(
                context,
                requireContext().getString(R.string.empty_note_err),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    fun requestPermissionsStorage() {
        BottomSheetBehavior.from(binding.layoutOptions.layoutContainer).state =
            BottomSheetBehavior.STATE_COLLAPSED
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constraints.REQUEST_STORAGE_PERMISSION_CODE
            )
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initOnClickListener() = binding.apply {
        backContainer.setOnClickListener { backBtnPress() }
    }

    private fun saveNote() {
        if (isValidNoteData()) {
            noteViewModel.addNewNote(
                binding.edtNoteTitle.text.toString(),
                binding.edtNoteSubTitle.text.toString(),
                binding.edtNoteInput.text.toString(),
                binding.tvDatetime.text.toString()
            )

            iOSDialogBuilder(activity)
                .setTitle(activity?.getString(R.string.app_title))
                .setSubtitle(activity?.getString(R.string.add_success_confirm))
                .setPositiveListener(activity?.getString(R.string.confirm)) {
                    noteViewModel.resetNote()
                    backBtnPress()
                    it.dismiss()
                }
                .build().show()
        }
    }

    private fun backBtnPress() {
        activity?.onBackPressed()
        noteViewModel.resetNote()
    }

    private fun updateNote() {
        if (isValidNoteData()) {
            noteViewModel.updateNote(
                navigationArgs.id,
                binding.edtNoteTitle.text.toString(),
                binding.edtNoteSubTitle.text.toString(),
                binding.edtNoteInput.text.toString(),
                noteViewModel.getDateTime()
            )

            iOSDialogBuilder(activity)
                .setTitle(activity?.getString(R.string.app_title))
                .setSubtitle(activity?.getString(R.string.update_success_confirm))
                .setPositiveListener(activity?.getString(R.string.confirm)) {
                    noteViewModel.resetNote()
                    backBtnPress()
                    it.dismiss()
                }
                .build().show()
        }
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