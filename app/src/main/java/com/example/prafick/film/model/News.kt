package com.example.prafick.film.model

class News(private var title: String, private var description: String, private var pic: String) {

    fun getTitle(): String {
        return title
    }

    fun getDescription(): String {
        return description
    }

    fun getPic(): String {
        return pic
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun setPic(pic: String) {
        this.pic = pic
    }
}