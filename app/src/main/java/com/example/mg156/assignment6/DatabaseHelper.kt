package com.example.mg156.assignment6

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.Gson
import java.util.ArrayList

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VER) {
    companion object {
        private val DB_NAME = " mymovies"

        private val TABLE_MOVIES = "movies"
        private val TABLE_GENRES = "genres"
        private val TABLE_MOVIE_GENRE = "movie_genre"

        private val DB_VER = 1
        private val COL_VOTE_CNT = "vote_count"
        private val COL_ID = "id"
        private val COL_VOTE_AVG = "vote_average"
        private val COL_TITLE = "title"
        private val COL_POPULARITY = "popularity"
        private val COL_POSTER = "poster_path"
        private val COL_ORG_LANG = "original_language"
        private val COL_ORG_TITLE = "original_title"
        private val COL_BACKDROP = "backdrop_path"
        private val COL_OVERVIEW = "overview"
        private val COL_RELEASE = "release_date"
        private val COL_GENRE = "genre_name"
        private val COL_MOVIE_ID = "movie_id"
        private val COL_GENRE_ID = "genre_id"
    }

    private val CREATE_TABLE_MOVIES = " CREATE TABLE IF NOT EXISTS $TABLE_MOVIES ( " +
            "$COL_ID INTEGER PRIMARY KEY," +
            "$COL_MOVIE_ID INTEGER ," +
            "$COL_TITLE TEXT , " +
            "$COL_VOTE_AVG REAL , " +
            "$COL_VOTE_CNT INTEGER ," +
            "$COL_POSTER TEXT ," +
            "$COL_BACKDROP TEXT , " +
            "$COL_POPULARITY REAL ," +
            "$COL_OVERVIEW TEXT ," +
            "$COL_ORG_LANG TEXT ," +
            "$COL_ORG_TITLE TEXT ," +
            "$COL_RELEASE TEXT )"


    private val CREATE_TABLE_GENRE = " CREATE TABLE IF NOT EXISTS $TABLE_GENRES ( " +
            "$COL_ID INTEGER PRIMARY KEY ," +
            "$COL_GENRE TEXT )"

    private val CREATE_TABLE_MOVIE_GENRES = " CREATE TABLE IF NOT EXISTS $TABLE_MOVIE_GENRE ( " +
            "$COL_ID INTEGER PRIMARY KEY ," +
            "$COL_MOVIE_ID INTEGER ," +
            "$COL_GENRE_ID INTEGER )"


    private val DROP_TABLE_MOVIES = "DROP TABLE IF EXISTS movies "
    private val DROP_TABLE_GENRES = "DROP TABLE IF EXISTS genres "
    private val DROP_TABLE_MOVIE_GENRES = "DROP TABLE IF EXISTS movie_genres "

    override fun onCreate (db: SQLiteDatabase?) {
        db!!.execSQL(CREATE_TABLE_MOVIES)
        db!!.execSQL(CREATE_TABLE_GENRE )
        db!!.execSQL(CREATE_TABLE_MOVIE_GENRES )
    }

    override fun onUpgrade (db: SQLiteDatabase?, oldVersion : Int , newVersion : Int) {
        db!!.execSQL(DROP_TABLE_MOVIES)
        db!!.execSQL(DROP_TABLE_GENRES)
        db!!.execSQL(DROP_TABLE_MOVIE_GENRES)
    }

    fun initializeTables() {
        val db = this.readableDatabase
        var query: String = " SELECT * FROM genres "
        var c = db.rawQuery(query, null)
        if (c.count <= 0) {
            insertAllGenres()
        }
        query = " SELECT * FROM movies "
        c = db.rawQuery(query, null)
        if (c.count <= 0) {
            insertAllMovies()
        }

    }

    fun createNewMovie(movie: MovieData, genres: List<Int?>?): Int {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COL_MOVIE_ID, movie.id)
        values.put(COL_VOTE_AVG, movie.vote_average)
        values.put(COL_TITLE, movie.title)
        values.put(COL_ORG_TITLE, movie.original_title)
        values.put(COL_ORG_LANG, movie.original_language)
        values.put(COL_OVERVIEW, movie.overview)
        values.put(COL_POPULARITY, movie.popularity)
        values.put(COL_POSTER, movie.poster_path)
        values.put(COL_BACKDROP, movie.backdrop_path)
        values.put(COL_VOTE_CNT, movie.vote_count)
        values.put(COL_RELEASE, movie.release_date)
        val index = db.insert(TABLE_MOVIES, null, values).toInt()

        for (gid in genres!!) {
            val values1 = ContentValues()
            values1.put(COL_MOVIE_ID, movie.id)
            values1.put(COL_GENRE_ID, gid)
            val index1 = db.insert(TABLE_MOVIE_GENRE, null, values1).toInt()
        }
        closeDB()
        return index
    }

    fun copyMovies(movie: MovieData) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_MOVIE_ID, movie.id)
        values.put(COL_VOTE_AVG, movie.vote_average)
        values.put(COL_TITLE, movie.title)
        values.put(COL_ORG_TITLE, movie.original_title)
        values.put(COL_ORG_LANG, movie.original_language)
        values.put(COL_OVERVIEW, movie.overview)
        values.put(COL_POPULARITY, movie.popularity)
        values.put(COL_POSTER, movie.poster_path)
        values.put(COL_BACKDROP, movie.backdrop_path)
        values.put(COL_VOTE_CNT, movie.vote_count)
        values.put(COL_RELEASE, movie.release_date)
        val index = db.insert(TABLE_MOVIES, null, values).toInt()
        closeDB()
    }

    fun insertAllMovies() {
        var movieList: MutableList<MovieData> = Gson().fromJson(movies , Array <MovieData>::class.java ).toMutableList()
        for(movie in movieList){
            createNewMovie(movie, movie.genre_ids)
        }
    }

    fun createNewGenre(id: Int, genre: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID, id)
        values.put(COL_GENRE, genre)
        return db.insert(TABLE_GENRES, null, values).toInt()
    }

    fun insertAllGenres() {
        genresList = Gson().fromJson(genres , Array <GenreData>::class.java ).toMutableList()
        for(genre in genresList){
            createNewGenre(genre.id!!, genre.name!!)
        }
    }

    fun getAllGenres() : ArrayList<GenreData>{
        val genres = ArrayList<GenreData>()

        val query = " SELECT * FROM $TABLE_GENRES"
        val db = this.readableDatabase
        val c = db.rawQuery(query, null)

        if (c.moveToFirst()) {
            do {
                val genre = GenreData(
                        id = c.getInt(0),
                        name = c.getString(1)
                )
                genres.add(genre)
            } while (c.moveToNext())
        }
        return genres
    }

    fun getMovie(mid: Int): MovieData? {
        val query = " SELECT * FROM movies WHERE $COL_ID = $mid "
        val db = this.readableDatabase
        val c = db.rawQuery(query, null)
        if (c.moveToFirst()) {
            val movie = MovieData(
                    db_id = c.getInt(0),
                    id = c.getInt(1),
                    title = c.getString(2),
                    vote_average = c.getDouble(3),
                    vote_count = c.getInt(4),
                    poster_path = c.getString(5),
                    backdrop_path = c.getString(6),
                    popularity = c.getDouble(7),
                    overview = c.getString(8),
                    original_language = c.getString(9),
                    original_title = c.getString(10),
                    release_date = c.getString(11),
                    genre_ids = null
            )
            return movie
        } else
            return null
    }

    fun getAllMovies(): MutableList<MovieData>? {
        val movies = ArrayList<MovieData>()

        val query = " SELECT * FROM $TABLE_MOVIES"
        val db = this.readableDatabase
        val c = db.rawQuery(query, null)

        if (c.moveToFirst()) {
            do {
                val movie = MovieData(
                        db_id = c.getInt(0),
                        id = c.getInt(1),
                        title = c.getString(2),
                        vote_average = c.getDouble(3),
                        vote_count = c.getInt(4),
                        poster_path = c.getString(5),
                        backdrop_path = c.getString(6),
                        popularity = c.getDouble(7),
                        overview = c.getString(8),
                        original_language = c.getString(9),
                        original_title = c.getString(10),
                        release_date = c.getString(11),
                        genre_ids = null
                )
                movies.add(movie)
            } while (c.moveToNext())
        }
        return movies
    }

    fun getMatchedGenres(mid: Int): List<GenreData>? {
        val db = this.readableDatabase
        val query = " SELECT movie_genre.genre_id as id, genres.genre_name as genre  FROM movie_genre  JOIN genres on movie_genre.genre_id = genres.id  WHERE movie_genre.movie_id =$mid"
        val c = db.rawQuery(query, null)
        if (c.moveToFirst()) {
            val gs = ArrayList<GenreData>()
            do {
                val g = GenreData(c.getInt(0),c.getString(1))
                gs.add(g)
            } while (c.moveToNext())
            return gs
        }
        return null
    }

    fun deleteMovie(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_MOVIES, " $COL_MOVIE_ID = ?",
                arrayOf(id.toString()))
        db.close()
    }


    fun closeDB() {
        val db = this.readableDatabase
        if (db != null && db.isOpen)
            db.close()
    }
}