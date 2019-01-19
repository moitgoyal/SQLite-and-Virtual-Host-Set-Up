package com.example.mg156.assignment6

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.Toast
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import java.io.Serializable


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RecyclerViewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    lateinit var recycleView: RecyclerView
    lateinit var recycleViewAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    inner class ActionBarCallBack(internal var position: Int) : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            val id = item.itemId
            when (id) {
                R.id.contextual_or_pop_menu_delete -> {
                    recycleViewAdapter.removeMovie(position)
                    recycleViewAdapter.notifyItemRemoved(position)
                    mode.finish()
                }
                R.id.contextual_or_pop_menu_copy -> {
                    recycleViewAdapter.addMovie(position)
                    recycleViewAdapter.notifyItemInserted(position+1)
                    mode.finish()
                }
                else -> {
                }
            }
            return false
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.contextual_or_popmenu, menu)
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode) {}
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.title = recycleViewAdapter.getTitle(position)
            return false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootview = inflater.inflate(R.layout.fragment_recycler_view, container, false)

        recycleView = rootview.findViewById(R.id.recyclerViewId) as RecyclerView
        recycleViewAdapter =  RecyclerViewAdapter(context!!)
        recycleView.setAdapter(recycleViewAdapter)
        recycleView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context)
        recycleView.setLayoutManager(mLayoutManager)

        recycleView.itemAnimator?.addDuration = 2000L
        recycleView.itemAnimator?.removeDuration = 3000L
        recycleView.itemAnimator?.changeDuration = 1000L
        recycleView.itemAnimator?.moveDuration = 1000L

        recycleViewAdapter.setMyItemClickListener(object : RecyclerViewAdapter.MyItemClickListener {
            override fun onItemClickedFromAdapter(movie : MovieData) {
                listener?.onFragmentInteraction(movie)
            }

            override fun onItemLongClickedFromAdapter(position: Int) {
                val actionBar = ActionBarCallBack(position)
                val appActivity = activity as AppCompatActivity?
                appActivity!!.startSupportActionMode(actionBar)
            }

            override fun onOverFlowMenuClick(view: View, position: Int) {
                val popup = PopupMenu(activity!!, view)
                popup.setOnMenuItemClickListener { item ->
                    val id = item.itemId
                    when (id) {
                        R.id.contextual_or_pop_menu_copy -> {
                            recycleViewAdapter.addMovie(position)
                            recycleViewAdapter.notifyItemInserted(position+1)
                            true
                        }
                        R.id.contextual_or_pop_menu_delete -> {
                            recycleViewAdapter.removeMovie(position)
                            recycleViewAdapter.notifyItemRemoved(position)
                            true
                        }
                        else -> false
                    }
                }
                val inflater = popup.menuInflater
                inflater.inflate(R.menu.contextual_or_popmenu, popup.menu)
                popup.show()
            }
        })

        return rootview
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (menu!!.findItem(R.id.recycle_fragment_toolbar) == null) {
            inflater!!.inflate(R.menu.recycle_fragment_toolbar, menu)
        }
        var search: SearchView? = null

        if (menu.findItem(R.id.recycle_fragment_actionview) == null) {
            inflater!!.inflate(R.menu.recycle_fragment_actionview, menu)
        }
        val menuItem = menu.findItem(R.id.recycle_fragment_searchitem)
        if (menuItem != null) {
            search = menuItem.actionView as SearchView
        }
        search?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val position = recycleViewAdapter.findGenreList(query,recycleViewAdapter)
                recycleViewAdapter.notifyDataSetChanged()
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                recycleViewAdapter.resetMovies()
                recycleViewAdapter.notifyDataSetChanged()
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(movie : MovieData)
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String) : RecyclerViewFragment{
            val args = Bundle()
            args.putSerializable(param1,"RecyclerFragment")
            val fragment = RecyclerViewFragment()
            fragment.arguments = args
            return fragment
        }

    }


    override fun onSaveInstanceState(content: Bundle) {
        super.onSaveInstanceState(content)
    }
}
