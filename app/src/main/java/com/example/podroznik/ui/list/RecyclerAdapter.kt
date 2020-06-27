package com.example.podroznik.ui.list

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.podroznik.R
import com.example.podroznik.ui.DetailsActivity
import kotlinx.android.synthetic.main.list_view_item.view.*

class RecyclerAdapter(
    private val fragment: ListFragment,
    private val places: MutableList<Place>
): RecyclerView.Adapter<RecyclerAdapter.PlaceHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.PlaceHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.list_view_item, parent, false)
        return PlaceHolder(fragment, inflatedView)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.PlaceHolder, position: Int) {
        val place = places[position]
        holder.bindPlace(place)
    }

    class PlaceHolder(private val fragment: ListFragment, v: View): RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v
        private var place: Place? = null

        companion object {
            private val placeKey = "PLACE"
        }

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.d("Recycler view", "CLICK! ${this.place}")

            val intent = Intent(v.context, DetailsActivity::class.java)
            intent.putExtra("place", this.place)
            fragment.startActivityForResult(intent, 10002)
        }

        fun bindPlace(place: Place) {
            this.place = place
            view.item_name.text = this.place!!.placeName
            view.item_diameter.text = fragment.getString(R.string.list_diameter_text, this.place!!.placeDiameter.toString())
            view.item_photo.setImageBitmap(BitmapFactory.decodeByteArray(place.placePhoto, 0, place.placePhoto.size))
        }
    }
}
