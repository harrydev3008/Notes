package com.hisu.notes.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.hisu.notes.repository.NoteRepository

class NoteViewModel(val noteRepository: NoteRepository) : ViewModel() {
}