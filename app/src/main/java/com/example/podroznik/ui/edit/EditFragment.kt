package com.example.podroznik.ui.edit

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.podroznik.AppState
import com.example.podroznik.R
import com.example.podroznik.ui.PlaceAction
import com.example.podroznik.ui.edit.strategy.CreateEditorStrategy
import com.example.podroznik.ui.edit.strategy.EditorStrategy
import com.example.podroznik.ui.edit.strategy.UpdateEditorStrategy
import com.example.podroznik.ui.list.Place
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditFragment : Fragment() {

    lateinit var placePhoto: ImageView
    lateinit var placeName: EditText
    lateinit var placeNote: EditText
    lateinit var placeDiameter: EditText
    lateinit var buttonGetLoc: Button
    lateinit var buttonTakePhoto: FloatingActionButton
    lateinit var buttonSavePlace: Button
    lateinit var buttonAbandon: Button

    companion object {
        fun newInstance() = EditFragment()
    }

    private lateinit var viewModel: EditViewModel

    private lateinit var mPhotoLocation: String

    private lateinit var strategy: EditorStrategy

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.edit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placePhoto = view.findViewById(R.id.place_photo)
        placeName = view.findViewById(R.id.place_name)
        placeNote = view.findViewById(R.id.place_note)
        placeDiameter = view.findViewById(R.id.place_diameter)
        buttonGetLoc = view.findViewById(R.id.button_get_place_loc)
        buttonTakePhoto = view.findViewById(R.id.button_take_photo)
        buttonSavePlace = view.findViewById(R.id.button_save_place)
        buttonAbandon = view.findViewById(R.id.button_abandon)

        viewModel = ViewModelProviders.of(this).get(EditViewModel::class.java)

        placeName.hint = getString(R.string.place_name_input_placeholder)
        placeNote.hint = getString(R.string.place_note_input_placeholder)
        placeDiameter.hint = getString(R.string.place_diameter_input_placeholder)
        buttonGetLoc.text = getString(R.string.button_get_loc_text)
        buttonSavePlace.text = getString(R.string.button_save_place_text)
        buttonAbandon.text = getString(R.string.button_abandon_text)

        val action: PlaceAction = activity?.intent?.getSerializableExtra("action") as PlaceAction
        setStrategy(action, view)
        if (action == PlaceAction.EDIT_PLACE) {
            viewModel.place = activity?.intent?.getSerializableExtra("place") as Place
        }
        strategy.initForm()

        bindEvents()
    }

    private fun setStrategy(action: PlaceAction, view: View) {
        strategy = if (action == PlaceAction.ADD_PLACE) CreateEditorStrategy(view, this, viewModel) else UpdateEditorStrategy(view, this, viewModel)
    }

    private fun bindEvents() {

        buttonSavePlace.setOnClickListener {
            strategy.savePlace()
            activity!!.finish()
        }

        buttonAbandon.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.abandon_edit_dialog_title))
            builder.setMessage(getString(R.string.abandon_edit_dialog_message))

            builder.setPositiveButton("TAK") {_, _ ->
                activity?.finish()
            }

            builder.setNegativeButton("Nie") { dialog, _ -> dialog.cancel() }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        buttonTakePhoto.setOnClickListener {

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(activity!!.packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (e: IOException) { // Error occurred while creating the File
                    Log.e("EditActivity", "Unable to create photo file", e)
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    startActivityForResult(cameraIntent, 101)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = timeStamp + "filename"
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image: File = File.createTempFile(imageFileName, ".jpg", storageDir)
        mPhotoLocation = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                val photo = intent?.extras!!.get("data") as Bitmap
                val stream = ByteArrayOutputStream()
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()

                placePhoto.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
                println(byteArray.toString()) // TODO save byteArray to db
            }
        }
    }
}
