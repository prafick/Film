package com.example.prafick.film.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.prafick.film.R

class FilterAdapter(context: Context,
                      private val titles: ArrayList<String>, private val subtitles: ArrayList<String>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getCount(): Int {
        return titles.size
    }

    override fun getItem(position: Int): Any {
        return titles[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        // 1
        if (convertView == null) {

            // 2
            view = inflater.inflate(R.layout.row, parent, false)

            // 3
            holder = ViewHolder()
            holder.titleTextView = view.findViewById(R.id.titleLV) as TextView
            holder.subtitleTextView = view.findViewById(R.id.subtitleLV) as TextView
            // 4
            view.tag = holder
        } else {
            // 5
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        // 6
        val titleTextView = holder.titleTextView
        val subtitleTextView = holder.subtitleTextView

        titleTextView.text = titles[position]
        subtitleTextView.text = subtitles[position]

        return view
    }

    private class ViewHolder {
        lateinit var titleTextView: TextView
        lateinit var subtitleTextView: TextView
    }
}