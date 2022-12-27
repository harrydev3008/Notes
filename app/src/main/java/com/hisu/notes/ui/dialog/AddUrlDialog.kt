package com.hisu.notes.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.hisu.notes.databinding.LayoutAddUrlDialogBinding

class AddUrlDialog() {

    private lateinit var context: Context
    private var gravity: Int = 0
    private lateinit var dialog: Dialog
    private lateinit var binding: LayoutAddUrlDialogBinding

    constructor(context: Context, gravity: Int): this() {
        this.context = context
        this.gravity = gravity

        initDialog()
    }

    private fun initDialog() {
        binding = LayoutAddUrlDialogBinding.inflate(LayoutInflater.from(context), null, false)

        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        val window = dialog.window ?: return

        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttr = window.attributes
        windowAttr.gravity = gravity
        window.attributes = windowAttr

        binding.tvCancel.setOnClickListener { dismissDialog() }
    }

    fun showDialog() = dialog.show()
    fun dismissDialog() = dialog.dismiss()
    fun getUrl() = binding.edtGroupName.text.toString()

    fun addUrlEvent(callback: () -> Unit) {
        binding.tvConfirm.setOnClickListener { callback() }
    }
}