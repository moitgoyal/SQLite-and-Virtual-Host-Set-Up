package com.example.mg156.assignment6

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.coroutines.experimental.coroutineContext



class RecyclerViewAdapter(context: Context) : RecyclerView.Adapter<RecyclerViewAdapter.MovieViewHolder>() {

    var myListener : MyItemClickListener ? = null
    var lastPosition = -1

    val myDb = DatabaseHelper(context)
    lateinit var movieList: MutableList<MovieData>

    init {
        myDb.initializeTables()
        movieList = myDb.getAllMovies()!!
    }

    interface MyItemClickListener {
        fun onItemClickedFromAdapter (movie : MovieData )
        fun onItemLongClickedFromAdapter ( position : Int )
        fun onOverFlowMenuClick(view: View, position: Int)
    }

    fun setMyItemClickListener ( listener : MyItemClickListener ) {
        this.myListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val v: View
        when (viewType) {
            1 -> v = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
            2 -> v = LayoutInflater.from(parent.context).inflate(R.layout.item_row2, parent, false)
            3 -> v = LayoutInflater.from(parent.context).inflate(R.layout.item_row3, parent, false)
            else -> v = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        }
        return MovieViewHolder(v)
    }

    override fun getItemCount () : Int {
        return movieList.size
    }

    override fun onBindViewHolder ( holder : MovieViewHolder , position : Int ) {

        Log.d("Assignment 6 Testing", "Current Movie Count in Database = " + movieList.size.toInt().toString())
        val movie = movieList[position]

        val genres = myDb.getMatchedGenres(movie.id!!)

        var genreString = ""
        val i = 0
        for (genre in genres!!) {
            genreString += genre.name + ", "
        }

        genreString = genreString.trim().trimEnd(',')
        holder.movieGenre.setText(genreString)
        val url = "https://image.tmdb.org/t/p/w780/" + movie!!.poster_path!!
        Picasso.get().load(url).fit().into(holder.moviePoster)
        holder.movieTitle.text = movie.title
        holder.movieOverview.text = movie.overview
        setAnimation(holder.moviePoster , position )
    }

    fun findGenreList(query: String, myRecyclerViewAdapter: RecyclerViewAdapter): Int {
        var genreIsPresent = false
        var i = 0
        while (i < movieList.size) {
            val movie = movieList[i]
            val genres = myDb.getMatchedGenres(movie.id!!)
            for (genre in genres!!) {
                if (genre.name!!.toLowerCase().contains(query.toLowerCase()) === true || query.toLowerCase() == "all".toLowerCase()) {
                    genreIsPresent = true
                }
            }
            if (!genreIsPresent) {
                movieList.removeAt(i)
                myRecyclerViewAdapter.notifyItemRemoved(i)
                i--
            }
            genreIsPresent = false
            i++
        }
        return 0
    }


    fun setAnimation ( view : View , position : Int ) {
        if ( position != lastPosition ) {
            var animation =  AnimationUtils.loadAnimation( view.context, android.R.anim.slide_in_left);
            animation.setDuration(1000);
            view.startAnimation(animation) ;
            lastPosition = position
        }
    }

    fun getTitle(position: Int) : String{
        return movieList[position].title!!
    }

    fun resetMovies(){
        movieList = myDb.getAllMovies()!!
    }

    fun removeMovie(position: Int){

        var movie = movieList[position]
        var id = movie.id

        movieList.removeAt(position)
        myDb.deleteMovie(id!!)

        val mlist = myDb.getAllMovies()!!
        Log.d("Assignment 6 Testing", "Database after delete \n \n")
        for (i in mlist.indices) {
            Log.d("Assignment 6 Testing", mlist.get(i).toString())
        }
    }

    fun addMovie(position: Int){

        var movie = movieList[position]
        var id = movie.id

        movieList.add(position+1,movieList[position].copy())
        myDb.copyMovies(movie)

        val mlist = myDb.getAllMovies()!!
        Log.d("Assignment 6 Testing", "Database after copy \n \n")
        for (i in mlist.indices) {
            Log.d("Assignment 6 Testing", mlist.get(i).toString())
        }
    }

    override fun getItemViewType ( position : Int ): Int {
        return if (position < 3) {
            1
        } else if (position >= itemCount - 3) {
            3
        } else
            2
    }

    inner class MovieViewHolder ( view : View ) : RecyclerView.ViewHolder ( view ) {
        val moviePoster = view.findViewById <ImageView>(R.id.item_image )
        val movieTitle = view.findViewById <TextView>(R.id.item_title )
        val movieOverview = view.findViewById <TextView>(R.id.item_overview )
        val movieGenre = view.findViewById <TextView>(R.id.item_genre )
        val movie_overflow_image = view.findViewById <ImageView>(R.id.item_overflow_image)
        init {
            view.setOnClickListener(View.OnClickListener { v ->
                if (myListener != null) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        myListener!!.onItemClickedFromAdapter(movieList[adapterPosition])
                    }
                }
            })

            view.setOnLongClickListener(View.OnLongClickListener { v ->
                if (myListener != null) {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        myListener!!.onItemLongClickedFromAdapter(adapterPosition)
                    }
                }
                true
            })

            movie_overflow_image.setOnClickListener(View.OnClickListener { v ->
                myListener!!.onOverFlowMenuClick(v, adapterPosition) })
        }
    }
}