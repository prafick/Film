package com.example.prafick.film.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.method.ScrollingMovementMethod
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.prafick.film.R
import com.example.prafick.film.adapters.FilmAdapter
import com.example.prafick.film.model.Film
import kotlinx.android.synthetic.main.activity_recomendation_film.*
import org.json.JSONObject

class FilmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recomendation_film)
        supportActionBar?.hide()
        val title = intent.extras.getString("film_title")
        val description = intent.extras.getString("film_description")
        val id = intent.extras.getInt("film_id")
        val rating = intent.extras.getDouble("film_rating")
        val poster = intent.extras.getString("film_poster")
        val flag = intent.extras.getBoolean("flag")
        val collapsingToolBar = this.collapsing_toolbar
        collapsingToolBar.isTitleEnabled = true
        if (flag)
           collapsingToolBar.title = "Вам понравится"
        else
            collapsingToolBar.title = "Фильмы"
        this.aa_title_film.text = title
        this.description.text = description
        if (flag)
            this.aa_rate.text = "Рейтинг: $rating"
        val option = RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.ic_terrain_black_48dp)
        Glide.with(this).load(poster).apply(option).into(this.aa_poster_film)
        val d = this.description
        d.movementMethod = ScrollingMovementMethod()
        if (flag)
            getRecommendationFilm(id)
        else
            getFilmList(id)
    }

    private fun getFilmList(idActor: Int) {
        val films = arrayListOf<Film>()
        val url = "https://api.themoviedb.org/3/person/$idActor/movie_credits?api_key=$api&language=ru"
        var titleFilm: String
        var film: JSONObject
        var posterFilm: String
        var description: String
        var idFilm: Int
        var rating: Double
        val queue = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->
            val filmArray = response.getJSONArray("cast")
            for (i in 0..filmArray.length()) {
                try {
                    film = filmArray.getJSONObject(i)
                    titleFilm = film.getString("title")
                    posterFilm = "http://image.tmdb.org/t/p/w154/" +
                            film.getString("poster_path")
                    description = film.getString("overview")
                    idFilm = film.getInt("id")
                    rating = film.getDouble("vote_average")
                    films.add(Film(titleFilm, posterFilm, description, idFilm, rating))
                } catch (e: Exception) {
                }
            }
            setupRecycleView(films)
        }, Response.ErrorListener { })
        requestQueue?.add(queue)
    }

    private fun setupRecycleView(films: ArrayList<Film>) {
        val adapter = FilmAdapter(this, films)
        val recyclerView = this.recommendation_list
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun getRecommendationFilm(id: Int): ArrayList<Film> {
        val films = arrayListOf<Film>()
        val url = "https://api.themoviedb.org/3/movie/$id/recommendations?api_key=$api&language=ru&page=1"
        var titleFilm: String
        var film: JSONObject
        var posterFilm: String
        var description: String
        var idFilm: Int
        var rating: Double
        val queue = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->
            val pages = response.getInt("total_pages")
            if (pages == 1) {
                val filmArray = response.getJSONArray("results")
                for (i in 0..filmArray.length()) {
                    try {
                        film = filmArray.getJSONObject(i)
                        titleFilm = film.getString("title")
                        posterFilm = "http://image.tmdb.org/t/p/w154/" +
                                film.getString("poster_path")
                        description = film.getString("overview")
                        idFilm = film.getInt("id")
                        rating = film.getDouble("vote_average")
                        films.add(Film(titleFilm, posterFilm, description, idFilm, rating))
                    } catch (e: Exception) {
                    }
                }
                setupRecycleView(films)
            } else {
                for (j in 1..pages) {
                    val newURL = url.replace(Regex("page=.*"), "page=$j")
                    val queue2 = JsonObjectRequest(Request.Method.GET, newURL, null, Response.Listener { response2 ->
                        val filmArray = response2.getJSONArray("results")
                        for (i in 0..filmArray.length()) {
                            try {
                                film = filmArray.getJSONObject(i)
                                titleFilm = film.getString("title")
                                posterFilm = "http://image.tmdb.org/t/p/w154/" +
                                        film.getString("poster_path")
                                description = film.getString("overview")
                                idFilm = film.getInt("id")
                                rating = film.getDouble("vote_average")
                                films.add(Film(titleFilm, posterFilm, description, idFilm, rating))
                            } catch (e: Exception) {
                                break
                            }

                        }
                        setupRecycleView(films)
                    }, Response.ErrorListener { })
                    requestQueue?.add(queue2)
                }
            }
        }, Response.ErrorListener { })
        requestQueue?.add(queue)
        return films
    }
}