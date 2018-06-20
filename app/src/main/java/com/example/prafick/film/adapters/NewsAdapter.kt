package com.example.prafick.film.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.prafick.film.R
import com.example.prafick.film.model.News

class NewsAdapter(private val mContext: Context, private val mData:List<News>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    private val option = RequestOptions().centerCrop()
            .placeholder(R.drawable.loading_shape)
            .error(R.drawable.ic_terrain_black_48dp)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(R.layout.row_news_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTitle?.text = mData[position].getTitle()
        Glide.with(mContext).load(mData[position].getPic()).apply(option).into(holder.mPic!!)
    }


    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val mTitle = itemView?.findViewById<TextView>(R.id.titleNews)
        val mPic = itemView?.findViewById<ImageView>(R.id.picNews)
    }
}