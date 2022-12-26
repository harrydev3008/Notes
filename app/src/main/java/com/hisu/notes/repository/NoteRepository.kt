package com.hisu.notes.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.hisu.notes.dao.NoteDAO
import com.hisu.notes.model.Note

class NoteRepository(context: Context, val noteDAO: NoteDAO) {

    fun getAllNotes(): LiveData<List<Note>> = noteDAO.getAllNotes()

    fun getNoteByID(id: String): LiveData<Note> = noteDAO.getNoteByID(id)

    fun searchNote(title: String): LiveData<List<Note>> = noteDAO.searchNote(title)

    suspend fun insertNote(note: Note) = noteDAO.insert(note)

    suspend fun updateNote(note: Note) = noteDAO.update(note)

    suspend fun deleteNote(note: Note) = noteDAO.delete(note)
}