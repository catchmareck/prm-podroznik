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
import com.example.podroznik.ui.EditActivity
import com.example.podroznik.ui.PlaceAction
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

        bindEvents()

        viewModel.getPlaces().observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if(::adapter.isInitialized)
                adapter.notifyDataSetChanged()

            initializeRecyclerView(view)
        })
    }

    private fun bindEvents() {

        addPlaceBtn.setOnClickListener {
            val intent = Intent(context, EditActivity::class.java)
            intent.putExtra("action", PlaceAction.ADD_PLACE)
            startActivityForResult(intent, 10001)
        }
    }

    private fun initializeRecyclerView(view: View) {

        linearLayoutManager = LinearLayoutManager(view.context)
        adapter = RecyclerAdapter(this, viewModel.getPlaces().value as MutableList<Place>)
        recyclerView = view.findViewById(R.id.places_list)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == 10001 || requestCode == 10002) && resultCode == Activity.RESULT_OK) {
            viewModel.loadPlaces()
        }
    }
}
