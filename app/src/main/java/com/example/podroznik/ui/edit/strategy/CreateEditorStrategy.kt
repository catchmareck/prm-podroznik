package com.example.podroznik.ui.edit.strategy

import android.graphics.BitmapFactory
import android.view.View
import com.example.podroznik.ui.edit.EditFragment
import com.example.podroznik.ui.edit.EditViewModel
import com.example.podroznik.ui.list.Place
import kotlinx.android.synthetic.main.edit_fragment.*
import org.json.JSONArray

class CreateEditorStrategy(view: View, fragment: EditFragment, viewModel: EditViewModel) : EditorStrategy(view, fragment, viewModel) {

    override fun initForm() {
        fragment.place_photo.setImageBitmap(BitmapFactory.decodeByteArray(ByteArray(0), 0, 0))
        fragment.place_name.setText("")
        fragment.place_note.setText("")
        fragment.place_diameter.setText("0")
        fragment.lat = 0.0
        fragment.lon = 0.0
    }

    override fun clearForm() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun savePlace() {
        val name: String = fragment.place_name.text.toString()
        val note: String = fragment.place_note.text.toString()
        val diameter: Double = fragment.place_diameter.text.toString().toDouble()
        val lon = fragment.lat
        val lat = fragment.lon

        val place = Place(0, name, note, diameter, lat, lon, JSONArray("[]"))
        place.placePhoto = getImageBytes()

        viewModel.place = place
        viewModel.createPlace()
    }
}
