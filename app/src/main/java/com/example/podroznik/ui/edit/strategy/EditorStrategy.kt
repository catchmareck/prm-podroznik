package com.example.podroznik.ui.edit.strategy

import android.graphics.Bitmap
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.example.podroznik.ui.edit.EditFragment
import com.example.podroznik.ui.edit.EditViewModel
import kotlinx.android.synthetic.main.edit_fragment.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

abstract class EditorStrategy(val view: View, val fragment: EditFragment, val viewModel: EditViewModel) {

    abstract fun initForm()

    abstract fun clearForm()

    abstract fun savePlace()

    protected fun getImageBytes(): ByteArray {
        return try {
            val bitmap = fragment.place_photo.drawable.toBitmap()
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            ByteArray(0)
        }
    }
}
