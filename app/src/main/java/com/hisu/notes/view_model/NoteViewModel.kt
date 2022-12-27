package com.hisu.notes.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hisu.notes.model.Note
import com.hisu.notes.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel(val noteRepository: NoteRepository) : ViewModel() {

    private val _noteColor = MutableLiveData<String>()
    val noteColor: LiveData<String> get() = _noteColor

    private val _imagePath = MutableLiveData<String?>()

    private val _url = MutableLiveData<String?>()
    val url: LiveData<String?> get() = _url

    fun getDateTime(): String {
        //output ex: thứ hai, 26 tháng 12 năm 2022 17:17 CH
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    fun getNote(id: String) = noteRepository.getNoteByID(id)

    fun getAllNotes() = noteRepository.getAllNotes()

    fun searchNote(title: String) = noteRepository.searchNote("%$title%")

    fun addNewNote(title: String, subtitle: String, text: String, datetime: String) {

        val note = Note(
            title = title,
            subtitle = subtitle,
            text = text,
            dateTime = datetime,
            imagePath = _imagePath.value,
            color = _noteColor.value,
            url = _url.value
        )

        insertNote(note)
    }

    fun updateNote(id: Int, title: String, subtitle: String, text: String, datetime: String) {

        val note = Note(
            id = id,
            title = title,
            subtitle = subtitle,
            text = text,
            dateTime = datetime,
            imagePath = _imagePath.value,
            color = _noteColor.value,
            url = _url.value
        )

        updateNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.deleteNote(note)
    }

    fun setNoteColor(color: String) {
        _noteColor.value = color
    }

    fun setNoteImage(imagePath: String?) {
        _imagePath.value = imagePath
    }

    fun setNoteURL(url: String?) {
        _url.value = url
    }

    private fun insertNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.insertNote(note)
    }

    private fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.updateNote(note)
    }

    fun resetNote() {
        _noteColor.value = "#333333"
        _imagePath.value = null
        _url.value = null
    }
}