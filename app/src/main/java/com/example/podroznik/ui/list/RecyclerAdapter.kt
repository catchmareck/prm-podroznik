package com.example.podroznik.ui.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.podroznik.R
import kotlinx.android.synthetic.main.list_view_item.view.*

class RecyclerAdapter(private val places: List<String>): RecyclerView.Adapter<RecyclerAdapter.PlaceHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.PlaceHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.list_view_item, parent, false)
        return PlaceHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.PlaceHolder, position: Int) {
        val place = places[position]
        holder.bindPlace(place)
    }

    class PlaceHolder(v: View): RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v
        private var place: String? = null

        companion object {
            private val placeKey = "PLACE"
        }

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("Recycler view", "CLICK! ${this.place}")
        }

        fun bindPlace(category: String) {
            this.place = category
            view.item_name.text = this.place
        }
    }
}
