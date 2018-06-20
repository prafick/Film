package com.example.prafick.film

import android.os.AsyncTask
import com.example.prafick.film.model.Film

class GetFilmListTask(private val handler: () -> ArrayList<Film>) : AsyncTask<Unit, Unit, ArrayList<Film>>() {
    override fun doInBackground(vararg params: Unit?): ArrayList<Film>? {
        return handler()
    }
}