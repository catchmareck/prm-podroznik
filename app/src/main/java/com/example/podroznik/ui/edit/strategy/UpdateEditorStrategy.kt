package com.example.podroznik.ui.edit.strategy

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.example.podroznik.ui.edit.EditFragment
import com.example.podroznik.ui.edit.EditViewModel
import kotlinx.android.synthetic.main.edit_fragment.*
import kotlinx.android.synthetic.main.edit_fragment.view.*
import java.io.ByteArrayOutputStream

class UpdateEditorStrategy(view: View, fragment: EditFragment, viewModel: EditViewModel) : EditorStrategy(view, fragment, viewModel) {

    override fun initForm() {
        val place = viewModel.place
        view.place_photo.setImageBitmap(BitmapFactory.decodeByteArray(place.placePhoto, 0, place.placePhoto.size))
        view.place_name.setText(place.placeName)
        view.place_note.setText(place.placeNote)
        view.place_diameter.setText(place.placeDiameter.toString())
    }

    override fun clearForm() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun savePlace() {
        val name: String = fragment.place_name.text.toString()
        val note: String = fragment.place_note.text.toString()
        val diameter: Double = fragment.place_diameter.text.toString().toDouble()
        val lon = 0.0 // TODO
        val lat = 0.0 // TODO
        val photo = getImageBytes()

        viewModel.updatePlace(name, note, diameter, lon, lat, photo)
    }
}