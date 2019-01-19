package com.example.mg156.assignment6

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.ShareActionProvider
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import java.io.Serializable








// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MovieFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MovieFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MovieFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var movie: MovieData

    lateinit var mshareActionProvider: ShareActionProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            movie = it.getSerializable(ARG_PARAM1) as MovieData
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val movieView = inflater.inflate(R.layout.fragment_movie, container, false)
        val movie_id = movieView.findViewById(R.id.movieId) as TextView
        val movie_title = movieView.findViewById(R.id.movieTitle) as TextView
        val movie_image = movieView.findViewById(R.id.movieImage) as ImageView
        val movie_overview = movieView.findViewById(R.id.movieOverview) as TextView
        val movie_language = movieView.findViewById(R.id.movieLanguage) as TextView
        val movie_year = movieView.findViewById(R.id.movieYear) as TextView

        movie_title.setText("Movie Title: " + movie?.title.toString())
        movie_overview.setText("Movie Overview:" + movie?.overview.toString())

        val url = "https://image.tmdb.org/t/p/w780/" + movie!!.poster_path!!
        Picasso.get().load(url).fit().into(movie_image)

        movie_year.setText("Release Date: " + movie?.release_date.toString())
        movie_language.setText("Movie Language: " + movie?.original_language.toString())
        movie_id.setText("Movie ID: " + movie?.id.toString())

        return movieView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (menu!!.findItem(R.id.movie_fragment_menu) == null) {
            inflater!!.inflate(R.menu.movie_fragment_toolbar, menu)
        }
        val menuItem2 = menu.findItem(R.id.movie_fragment_toolbar_title)
        val menuItem3 = menu.findItem(R.id.movie_fragment_action_provider)
        menuItem2?.setTitle(movie.title)
        if (menuItem3 != null) {
            mshareActionProvider = MenuItemCompat.getActionProvider(menuItem3) as ShareActionProvider
            val intentShare = Intent(Intent.ACTION_SEND)
            intentShare.type = "text/plain"
            intentShare.putExtra(Intent.EXTRA_TEXT, movie.title)
            mshareActionProvider.setShareIntent(intentShare)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: MovieData) : MovieFragment{
            val args = Bundle()
            args.putSerializable(ARG_PARAM1,param1 as Serializable)
            val fragment = MovieFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
