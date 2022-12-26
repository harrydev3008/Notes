package com.hisu.notes.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hisu.notes.repository.NoteRepository


class NoteViewModelProviderFactory (private val noteRepository: NoteRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(NoteViewModel::class.java))
            return NoteViewModel(noteRepository) as T

        throw IllegalArgumentException("Unknown Class Name Exception")
    }
}