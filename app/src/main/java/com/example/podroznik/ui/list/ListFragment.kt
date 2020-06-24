package com.example.podroznik.ui.list

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.podroznik.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ListFragment : Fragment() {

    internal lateinit var recyclerView: RecyclerView
    internal lateinit var addPlaceBtn: FloatingActionButton

    companion object {
        fun newInstance() = ListFragment()
    }

    private lateinit var viewModel: ListViewModel

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerAdapter

    private lateinit var mPhotoLocation: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.loadPlaces()

        addPlaceBtn = view.findViewById(R.id.add_place_btn)
        addPlaceBtn.setOnClickListener {

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(activity!!.packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (e: IOException) { // Error occurred while creating the File
                    Log.e("ChatActivity", "Unable to create photo file", e)
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    startActivityForResult(cameraIntent, 101)
                }
            }
        }

        viewModel.getPlaces().observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if(::adapter.isInitialized)
                adapter.notifyDataSetChanged()

            initializeRecyclerView(view)
        })
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
                println(photo)
                val stream = ByteArrayOutputStream()
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()

                println(byteArray.toString()) // TODO save byteArray to db
            }
        }
    }
    private fun initializeRecyclerView(view: View) {

        linearLayoutManager = LinearLayoutManager(view.context)
        adapter = RecyclerAdapter(viewModel.getPlaces().value as MutableList<Place>)
        recyclerView = view.findViewById(R.id.places_list)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL))
    }
}
