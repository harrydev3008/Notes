package com.hisu.notes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subtitle: String?,
    val text: String,
    @ColumnInfo(name = "date_time") val dateTime: String,
    @ColumnInfo(name = "image_path")val imagePath: String?,
    val color: String?,
    val url: String?
)