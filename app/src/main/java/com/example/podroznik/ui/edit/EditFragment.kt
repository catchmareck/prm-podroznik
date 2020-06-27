package com.example.podroznik.ui.edit

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
import androidx.lifecycle.Observer

class EditFragment : Fragment() {

    lateinit var placePhoto: ImageView
    lateinit var placeName: EditText
    lateinit var placeNote: EditText
    lateinit var placeDiameter: EditText
    lateinit var buttonGetLoc: Button
    lateinit var buttonTakePhoto: FloatingActionButton
    lateinit var buttonSavePlace: Button
    lateinit var buttonAbandon: Button

    var lat: Double = 0.0
    var lon: Double = 0.0

    companion object {
        fun newInstance() = EditFragment()
    }

    private lateinit var viewModel: EditViewModel

    private lateinit var mPhotoLocation: String

    private lateinit var strategy: EditorStrategy
    private var locationManager : LocationManager? = null

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

        locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager?

        subscribeLiveData()
        bindEvents()
    }

    private fun setStrategy(action: PlaceAction, view: View) {
        strategy = if (action == PlaceAction.ADD_PLACE) CreateEditorStrategy(view, this, viewModel) else UpdateEditorStrategy(view, this, viewModel)
    }

    private fun subscribeLiveData() {
        viewModel.created.observe(viewLifecycleOwner, Observer { change ->
            if (change == true) {
                activity?.finish()
                val toast = Toast.makeText(context, "Created", Toast.LENGTH_SHORT)
                toast.show()
            }
        })

        viewModel.edited.observe(viewLifecycleOwner, Observer { change ->
            if (change == true) {
                activity?.finish()
                val toast = Toast.makeText(context, "Saved", Toast.LENGTH_SHORT)
                toast.show()
            }
        })
    }

    private fun bindEvents() {

        buttonGetLoc.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
            }
        }

        buttonSavePlace.setOnClickListener {
            strategy.savePlace()
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
                if (photoFile != null) {
                    startActivityForResult(cameraIntent, 101)
                }
            }
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            lat = location.latitude
            lon = location.longitude

            val builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.get_loc_dialog_title))
            builder.setMessage(getString(R.string.get_loc_dialog_message))

            builder.setPositiveButton("OK") { dialog, _ -> dialog.cancel() }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        println(requestCode)
        println(grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                } catch (ex: SecurityException) {

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(getString(R.string.get_loc_failed_dialog_title))
                    builder.setMessage(getString(R.string.get_loc_failed_dialog_message))

                    builder.setPositiveButton("OK") { dialog, _ -> dialog.cancel() }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()
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
            }
        }
    }
}
