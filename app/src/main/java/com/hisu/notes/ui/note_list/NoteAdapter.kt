package com.hisu.notes.ui.note_list

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hisu.notes.databinding.LayoutNoteItemBinding
import com.hisu.notes.model.Note

class NoteAdapter(
    private val itemClickListener: (note: Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var notes: List<Note>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutNoteItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bindNoteData(note)
    }

    override fun getItemCount(): Int = notes.size

    inner class NoteViewHolder(val binding: LayoutNoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindNoteData(note: Note) = binding.apply {

            tvNoteTitle.text = note.title
            tvNoteSubtitle.text = note.subtitle
            tvNoteDatetime.text = note.dateTime

            if(note.imagePath != null) {
                noteImage.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
                noteImage.visibility = View.VISIBLE
            } else
                noteImage.visibility = View.GONE

            noteContainer.setOnClickListener { itemClickListener.invoke(note) }

            if(note.color != null)
                (noteContainer.background as GradientDrawable).setColor(Color.parseColor(note.color))
            else
                (noteContainer.background as GradientDrawable).setColor(Color.parseColor("#333333"))
        }
    }
}