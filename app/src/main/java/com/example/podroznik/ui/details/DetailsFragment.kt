package com.example.podroznik.ui.details

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.podroznik.R
import com.example.podroznik.ui.DetailsActivity
import com.example.podroznik.ui.EditActivity
import com.example.podroznik.ui.PlaceAction
import com.example.podroznik.ui.list.Place
import kotlinx.android.synthetic.main.details_fragment.view.*
import kotlinx.android.synthetic.main.list_view_item.view.item_name
import kotlinx.android.synthetic.main.list_view_item.view.item_photo

class DetailsFragment : Fragment() {

    lateinit var buttonEdit: Button
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

        buttonEdit = view.findViewById(R.id.button_edit_place)
        buttonDelete = view.findViewById(R.id.button_delete_place)
        buttonBack = view.findViewById(R.id.button_back_to_list)

        buttonEdit.text = getString(R.string.button_edit_place_text)
        buttonDelete.text = getString(R.string.button_delete_place_text)
        buttonBack.text = getString(R.string.button_back_text)

        viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
        viewModel.place = activity?.intent?.getSerializableExtra("place") as Place

        view.item_name.text = viewModel.place.placeName
        view.item_note.text = viewModel.place.placeNote
        view.item_photo.setImageBitmap(BitmapFactory.decodeByteArray(viewModel.place.placePhoto, 0, viewModel.place.placePhoto.size))

        subscribeLiveData()
        bindEvents()
    }

    private fun subscribeLiveData() {
        viewModel.deleted.observe(viewLifecycleOwner, Observer { change ->

            if (change == true) {
                activity?.finish()
                val toast = Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT)
                toast.show()
            }
        })
    }

    private fun bindEvents() {
        buttonBack.setOnClickListener {
            activity?.finish()
        }

        buttonDelete.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.place_delete_dialog_title))
            builder.setMessage(getString(R.string.place_delete_dialog_message, viewModel.place.placeName))

            builder.setPositiveButton("TAK") {_, _ ->
                viewModel.deletePlace()
            }

            builder.setNegativeButton("Nie") { dialog, _ -> dialog.cancel() }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        buttonEdit.setOnClickListener {
            val intent = Intent(context, EditActivity::class.java)
            intent.putExtra("place", viewModel.place)
            intent.putExtra("action", PlaceAction.EDIT_PLACE)
            startActivity(intent)
        }
    }
}
