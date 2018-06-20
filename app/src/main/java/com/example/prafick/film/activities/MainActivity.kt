@file:Suppress("DEPRECATION")

package com.example.prafick.film.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.prafick.film.GetFilmListTask
import com.example.prafick.film.R
import com.example.prafick.film.R.id.*
import com.example.prafick.film.adapters.FilmAdapter
import com.example.prafick.film.adapters.FilterAdapter
import com.example.prafick.film.model.Film
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

const val api = "f3036f3c9d963520afac674b4b6c71de"
var requestQueue: RequestQueue? = null

class MainActivity : AppCompatActivity() {
    private var minRating = 0
    private val subtitles = arrayListOf("Все жанры", "За все время", "Любой рейтинг")
    private val defaultTitles = arrayListOf("Выберите жанр", "Выберите год", "Выберите рейтинг")
    private val genre = arrayOf<CharSequence>("Боевик", "Вестерн", "Военный", "Детектив",
            "Документальный", "Драма", "История", "Комедия", "Криминал", "Мелодрама", "Музыка",
            "Мультфильм", "Приключения", "Семейный", "Телевизионный фильм", "Триллер", "Ужасы",
            "Фантастика", "Фэнтези")
    private val genreID = arrayOf(28, 37, 10752, 9648, 99, 18, 36, 35, 80, 10749, 10402, 16, 12,
            10751, 10770, 53, 27, 878, 14)
    private val titlesNews = arrayListOf("")
    private val pics = arrayListOf("")

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_watch -> {
                title = getString(R.string.title_watch)
                nameFilm.visibility = View.VISIBLE
                posterView.visibility = View.VISIBLE
                desc.visibility = View.VISIBLE
                rating.visibility = View.VISIBLE
                btn_rnd_film.visibility = View.VISIBLE
                searchList.visibility = View.VISIBLE
                newsList.visibility = View.INVISIBLE
                filmList.visibility = View.INVISIBLE
                recTitleFilm.visibility = View.INVISIBLE
                //getURL()
                showLayoutRndFilm()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_news -> {
                title = getString(R.string.title_news)
                nameFilm.visibility = View.INVISIBLE
                posterView.visibility = View.INVISIBLE
                desc.visibility = View.INVISIBLE
                rating.visibility = View.INVISIBLE
                btn_rnd_film.visibility = View.INVISIBLE
                searchList.visibility = View.INVISIBLE
                newsList.visibility = View.VISIBLE
                filmList.visibility = View.INVISIBLE
                recTitleFilm.visibility = View.INVISIBLE
                showLayoutNews()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_recommendation -> {
                title = getString(R.string.title_recommendation)
                nameFilm.visibility = View.INVISIBLE
                posterView.visibility = View.INVISIBLE
                desc.visibility = View.INVISIBLE
                rating.visibility = View.INVISIBLE
                btn_rnd_film.visibility = View.INVISIBLE
                newsList.visibility = View.INVISIBLE
                searchList.visibility = View.INVISIBLE
                filmList.visibility = View.VISIBLE
                recTitleFilm.visibility = View.VISIBLE
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        showLayoutRndFilm()

        desc.movementMethod = ScrollingMovementMethod()
        requestQueue = Volley.newRequestQueue(this)
        //getFilm()
        Random()
        val btn = findViewById<Button>(R.id.btn_rnd_film)
        btn.setOnClickListener {
            getRandomFilm()
            desc.scrollTo(0, 0)
        }
        recTitleFilm.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val task = GetFilmListTask { searchFilm() }.execute()
                val films = task.get()
                setupRecycleView(films)
                hideKeyboard()

                return@OnEditorActionListener true
            } else return@OnEditorActionListener false
        })
        //getNews()
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
    }

    private fun setupRecycleView(films: ArrayList<Film>) {
        val adapter = FilmAdapter(this, films)
        val recyclerView = findViewById<RecyclerView>(R.id.filmList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun searchFilm(): ArrayList<Film> {
        val films = arrayListOf<Film>()
        val url = "https://api.themoviedb.org/3/search/movie?api_key=$api&language=ru&include_adult=false" +
                "&query=${recTitleFilm.text}&page=1"
        var titleFilm: String
        var film: JSONObject
        var posterFilm: String
        var description: String
        var id: Int
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
                        id = film.getInt("id")
                        rating = film.getDouble("vote_average")
                        films.add(Film(titleFilm, posterFilm, description, id, rating))
                    } catch (e: Exception) {
                        break
                    }

                }
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
                                id = film.getInt("id")
                                rating = film.getDouble("vote_average")
                                films.add(Film(titleFilm, posterFilm, description, id, rating))
                            } catch (e: Exception) {
                                break
                            }

                        }
                    }, Response.ErrorListener { })
                    requestQueue?.add(queue2)
                }
            }
        }, Response.ErrorListener { })
        requestQueue?.add(queue)
        return films
    }

    private fun showLayoutNews() {
        //getNews()
        //newsList.adapter = NewsAdapter(this, titlesNews, pics)
    }

    @SuppressLint("InflateParams")
    private fun showLayoutRndFilm() {
        searchList.adapter = FilterAdapter(this, defaultTitles, subtitles)
        searchList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    val selectedItems = ArrayList<Int>()
                    AlertDialog.Builder(this)
                            .setTitle("Выбор жанра")
                            .setMultiChoiceItems(genre, null, { _, indexSelected, isChecked ->
                                if (isChecked) {
                                    selectedItems.add(indexSelected)
                                } else if (selectedItems.contains(indexSelected)) {
                                    selectedItems.remove(Integer.valueOf(indexSelected))
                                }
                            }).setPositiveButton("OK", { _, _ ->
                                subtitles[0] = ""
                                for (item: Int in selectedItems) {
                                    subtitles[0] += String.format("${genre[item]} ")
                                    searchList.adapter = FilterAdapter(this,
                                            defaultTitles, subtitles)
                                }
                            }).setNegativeButton("Сбросить", { _, _ ->
                                subtitles[0] = "Все жанры"
                                searchList.adapter = FilterAdapter(this,
                                        defaultTitles, subtitles)
                            }).create()
                            .show()
                }
                1 -> {
                    val dialog = AlertDialog.Builder(this)
                    val mView = layoutInflater.inflate(R.layout.dialog_years, null)
                    dialog
                            .setTitle("Выбор годов")
                            .setView(mView)
                            .setPositiveButton("OK", { _, _ ->
                                val minYear = mView.findViewById<EditText>(R.id.minYear)
                                val maxYear = mView.findViewById<EditText>(R.id.maxYear)
                                if (!minYear.text.isEmpty() && !maxYear.text.isEmpty())
                                    subtitles[1] = "${minYear.text} - " + "${maxYear.text}"
                                searchList.adapter = FilterAdapter(this,
                                        defaultTitles, subtitles)
                            })
                            .setNegativeButton("Сбросить", { _, _ ->
                                subtitles[1] = "За все время"
                                searchList.adapter = FilterAdapter(this,
                                        defaultTitles, subtitles)
                            }).create()
                            .show()
                }
                2 -> {
                    val dialog = AlertDialog.Builder(this)
                    val mView = layoutInflater.inflate(R.layout.dialog_rating, null)

                    dialog.setTitle("Минимальный рейтинг")
                            .setView(mView)
                            .setPositiveButton("OK", { _, _ ->
                                val minRate = mView.findViewById<SeekBar>(R.id.ratingBar)
                                if (minRate.progress != 0) {
                                    minRating = minRate.progress
                                    subtitles[2] = "Минимальный рейтинг: ${minRate.progress}"
                                    searchList.adapter = FilterAdapter(this,
                                            defaultTitles, subtitles)
                                }
                            }).setNegativeButton("Сбросить", { _, _ ->
                                minRating = 0
                                subtitles[2] = "Любой рейтинг"
                                searchList.adapter = FilterAdapter(this,
                                        defaultTitles, subtitles)
                            }).create()
                            .show()

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getRandomFilm() {
        val years = subtitles[1].replace(" ", "").split('-').toList()
        var url = if (years.size == 2)
            "https://api.themoviedb.org/3/discover/movie?api_key=$api" +
                    "&include_adult=false&language=ru&vote_average.gte=$minRating" +
                    "&primary_release_date.gte=${years[0]}&primary_release_date.lte=${years[1]}"
        else "https://api.themoviedb.org/3/discover/movie?api_key=$api" +
                "&include_adult=false&language=ru&vote_average.gte=$minRating"
        val listGenre = subtitles[0].split(' ').toList()
        for (item in listGenre) {
            if (genre.indexOf(item) != -1)
                url += "&with_genres=${genreID[genre.indexOf(item)]}"
        }
        val queue = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->
            val maxPage = if (response.getInt("total_pages") <= 1000)
                response.getInt("total_pages")
            else 1000
            url += "&page=${(1..maxPage).random()}"
            findFilm(url)
        }, Response.ErrorListener { })
        requestQueue?.add(queue)

    }

    @SuppressLint("NewApi", "SetTextI18n")
    private fun findFilm(url: String) {
        println(url)
        val films = arrayListOf<Film>()
        var titleFilm: String
        var film: JSONObject
        var posterFilm: String
        var description: String
        var id: Int
        var rating: Double

        val queue2 = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response2 ->
            val filmArray = response2.getJSONArray("results")
            for (i in 0..filmArray.length()) {
                try {
                    film = filmArray.getJSONObject(i)
                    titleFilm = film.getString("title")
                    posterFilm = "http://image.tmdb.org/t/p/w154/" +
                            film.getString("poster_path")
                    description = film.getString("overview")
                    id = film.getInt("id")
                    rating = film.getDouble("vote_average")
                    films.add(Film(titleFilm, posterFilm, description, id, rating))
                } catch (e: Exception) {
                }
            }

        }, Response.ErrorListener {

        })
        requestQueue?.add(queue2)
        val option = RequestOptions().centerCrop()
                .placeholder(R.drawable.loading_shape)
                .error(R.drawable.ic_terrain_black_48dp)
        val rndFilm = 0//0..films.size - 1).random()
        this.nameFilm.text = films[rndFilm].title
        this.desc.text = films[rndFilm].description
        this.rating.text = films[rndFilm].rating.toString()
        Glide.with(this).load(films[rndFilm].poster).apply(option).into(this.posterView)
    }

    private fun ClosedRange<Int>.random() =
            Random().nextInt(endInclusive - start) + start

    private fun getNews() {
        titlesNews.clear()
        pics.clear()
        val url = "https://www.kinonews.ru/news/"
        println("test1")
        val newsRequest = StringRequest(Request.Method.GET, url, Response.Listener { response ->
            val utf8 = String(response.toByteArray(), charset("windows-1251"))
                    .toByteArray(charset("UTF-8"))
            val responseUTF8 = String(utf8)
            val matches =
                    Regex("(height=100 width=75 alt=')(.*)('></a>)").findAll(responseUTF8)
            matches.forEach {
                titlesNews.add(it.groups[2]!!.value
                        .replace(Regex("&qout;"), "\""))
            }
            val posters = Regex("(<img src=\")(.*)(\" class)")
                    .findAll(responseUTF8)
            posters.forEach {
                pics.add("https://www.kinonews.ru${it.groups[2]!!.value.replace("p.jpg", ".jpg")}")
            }
        }, Response.ErrorListener {
        })
        requestQueue?.add(newsRequest)


    }

}