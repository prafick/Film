package com.example.prafick.film.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.prafick.film.R
import com.example.prafick.film.activities.FilmActivity
import com.example.prafick.film.model.Film

class FilmAdapter(private val mContext: Context, private val mData:List<Film>) : RecyclerView.Adapter<FilmAdapter.ViewHolder>(){

    private val option = RequestOptions().centerCrop()
            .placeholder(R.drawable.loading_shape)
            .error(R.drawable.ic_terrain_black_48dp)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_film_layout, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.viewContainer?.setOnClickListener({
            val i = Intent(mContext, FilmActivity().javaClass)
            i.putExtra("film_title", mData[viewHolder.adapterPosition].title)
            i.putExtra("film_poster", mData[viewHolder.adapterPosition].poster)
            i.putExtra("film_description", mData[viewHolder.adapterPosition].description)
            i.putExtra("film_id", mData[viewHolder.adapterPosition].id)
            i.putExtra("film_rating", mData[viewHolder.adapterPosition].rating)
            i.putExtra("flag", true)
                    mContext.startActivity(i)
        })
        return viewHolder
    }
    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTitle?.text = mData[position].title
        holder.mRating?.text = mData[position].rating.toString()
        Glide.with(mContext).load(mData[position].poster).apply(option).into(holder.mPic!!)
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val mTitle = itemView?.findViewById<TextView>(R.id.title_film)
        val mPic = itemView?.findViewById<ImageView>(R.id.poster_film)
        val mRating = itemView?.findViewById<TextView>(R.id.rate)
        val viewContainer = itemView?.findViewById<LinearLayout>(R.id.container)
    }
}