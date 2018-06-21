package com.example.prafick.film.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.prafick.film.R
import com.example.prafick.film.activities.FilmActivity
import com.example.prafick.film.model.Actor

class ActorAdapter(private val mContext: Context, private val mData: List<Actor>) : RecyclerView.Adapter<FilmAdapter.ViewHolder>() {
    private val option = RequestOptions().centerCrop()
            .placeholder(R.drawable.loading_shape)
            .error(R.drawable.ic_terrain_black_48dp)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_film_layout, parent, false)
        val viewHolder = FilmAdapter.ViewHolder(view)
        viewHolder.viewContainer?.setOnClickListener({
            val i = Intent(mContext, FilmActivity().javaClass)
            i.putExtra("film_title", mData[viewHolder.adapterPosition].name)
            i.putExtra("film_poster", mData[viewHolder.adapterPosition].photo)
            i.putExtra("film_id", mData[viewHolder.adapterPosition].id)
            i.putExtra("flag", false)
            mContext.startActivity(i)
        })
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: FilmAdapter.ViewHolder, position: Int) {
        holder.mTitle?.text = mData[position].name
        Glide.with(mContext).load(mData[position].photo).apply(option).into(holder.mPic!!)
    }
}