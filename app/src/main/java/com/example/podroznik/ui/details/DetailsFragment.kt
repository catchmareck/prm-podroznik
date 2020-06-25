package com.example.podroznik.ui.details

import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.podroznik.R
import com.example.podroznik.ui.list.Place
import kotlinx.android.synthetic.main.details_fragment.view.*
import kotlinx.android.synthetic.main.list_view_item.view.*
import kotlinx.android.synthetic.main.list_view_item.view.item_name
import kotlinx.android.synthetic.main.list_view_item.view.item_photo

class DetailsFragment : Fragment() {

    lateinit var buttonSave: Button
    lateinit var buttonDelete: Button
    lateinit var buttonBack: Button

    companion object {
        fun newInstance() = DetailsFragment()
    }

    private lateinit var viewModel: DetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonSave = view.findViewById(R.id.button_edit_place)
        buttonDelete = view.findViewById(R.id.button_delete_place)
        buttonBack = view.findViewById(R.id.button_back_to_list)

        buttonSave.text = getString(R.string.button_edit_place_text)
        buttonDelete.text = getString(R.string.button_delete_place_text)
        buttonBack.text = getString(R.string.button_back_text)

        viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
        viewModel.place = activity?.intent?.getSerializableExtra("place") as Place

        view.item_name.text = viewModel.place.placeName
        view.item_note.text = viewModel.place.placeNote
        view.item_photo.setImageBitmap(BitmapFactory.decodeByteArray(viewModel.place.placePhoto, 0, viewModel.place.placePhoto.size))

        bindEvents()
    }

    private fun bindEvents() {
        buttonBack.setOnClickListener {
            activity?.finish()
        }
    }
}
