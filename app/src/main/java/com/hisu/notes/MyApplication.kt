package com.hisu.notes

import android.app.Application
import com.hisu.notes.database.NoteDatabase

class MyApplication: Application() {
    val database: NoteDatabase by lazy { NoteDatabase.getDatabase(this) }
}