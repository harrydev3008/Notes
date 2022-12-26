package com.hisu.notes.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hisu.notes.model.Note

@Dao
interface NoteDAO {
    @Query("SELECT * FROM notes order by date_time desc")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes where title like :title")
    fun searchNote(title: String): LiveData<List<Note>>

    @Query("SELECT * FROM notes where id = :id")
    fun getNoteByID(id: String): LiveData<Note>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}