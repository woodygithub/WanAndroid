package com.wan.wanandroid.home

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.just.agentweb.AgentWeb
import com.wan.wanandroid.R
import com.wan.wanandroid.home.bean.Data
import com.wan.wanandroid.home.bean.Item
import com.wan.wanandroid.home.data.WanRepository
import com.wan.wanandroid.home.state.HomeListState
import kotlinx.android.synthetic.main.fragment_item_list.view.*
import me.yokeyword.fragmentation.SupportFragment

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class HomeItemFragment: SupportFragment(),HomeContract.View {
    override lateinit var presenter: HomeContract.Presenter

    lateinit var list:RecyclerView
    lateinit var refresh:SwipeRefreshLayout
    private lateinit var mListener: OnListFragmentInteractionListener
    private lateinit var mAgentWeb: AgentWeb

    override fun render(it: Data) {
        val adapter = list.adapter
        when(adapter){
            is ItemRecyclerViewAdapter ->{
                it.datas?.let{
                    if(HomeListState.page == 0) {
                        adapter.setValues(it)
                    }else{
                        adapter.appValues(it)
                    }
                }
            }
        }
        it.curPage?.let {
            HomeListState.page = it
        }
        if(refresh.isRefreshing()){
            refresh.setRefreshing(false)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = HomeListPresenter(WanRepository(),this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        // Set the adapter
        list = view.list
        refresh = view.refresh
        val context = view.getContext()
        list.setLayoutManager(LinearLayoutManager(context))
        list.adapter = ItemRecyclerViewAdapter(mListener)

        refresh.setOnRefreshListener {
            HomeListState.page = 0
            presenter.load()
        }
        refresh.setOnChildScrollUpCallback { parent, child ->
            child?.let {
                if(!child.canScrollVertically(1)){
                    presenter.load()
                    return@setOnChildScrollUpCallback true
                }
                return@setOnChildScrollUpCallback false
            }
            return@setOnChildScrollUpCallback false
        }
        return view
    }


    override fun onResume() {
        super.onResume()
        refresh.setRefreshing(true)
        presenter.start()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
            mListener =   object :OnListFragmentInteractionListener{
                override fun onListFragmentInteraction(item: Item){
                    Log.i("ss",item.title)
                   presenter.openDetails(item)
                }
            }
    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Item)
    }

    companion object {

        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int): HomeItemFragment {
            val fragment = HomeItemFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
