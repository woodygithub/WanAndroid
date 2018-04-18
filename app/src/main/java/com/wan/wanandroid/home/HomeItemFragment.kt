package com.wan.wanandroid.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ScrollView
import com.jakewharton.rxbinding2.support.v7.widget.dataChanges
import com.just.agentweb.AgentWeb
import com.wan.wanandroid.R
import com.wan.wanandroid.R.id.refresh
import com.wan.wanandroid.home.api.HomeList
import com.wan.wanandroid.home.bean.HomeBean
import com.wan.wanandroid.home.bean.Item
import com.wan.wanandroid.retrofit.RetrofitUtil
import com.wan.wanandroid.home.state.HomeListState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_item_list.view.*
import me.yokeyword.fragmentation.SupportFragment
import android.widget.LinearLayout




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
class HomeItemFragment :HomeView, SupportFragment() {

    lateinit var list:RecyclerView
    lateinit var refresh:SwipeRefreshLayout
    var page :Int = 0
    private var mColumnCount = 1
    private lateinit var mListener: OnListFragmentInteractionListener
    private lateinit var mAgentWeb: AgentWeb

    override fun render(homeListState: HomeListState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { mColumnCount = it.getInt(ARG_COLUMN_COUNT) }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        // Set the adapter
        list = view.list
        refresh = view.refresh
        val context = view.getContext()
        if (mColumnCount <= 1) {
            list.layoutManager = LinearLayoutManager(context)
        } else {
            list.layoutManager = GridLayoutManager(context, mColumnCount)
        }
        list.adapter = ItemRecyclerViewAdapter(mListener)

        refresh.setOnRefreshListener {
            get(page)
        }
        refresh.setOnChildScrollUpCallback { parent, child ->
            child?.let {
                if(!child.canScrollVertically(1)){
                    get(page)
                    return@setOnChildScrollUpCallback true
                }
                return@setOnChildScrollUpCallback false
            }
            return@setOnChildScrollUpCallback false
        }
        refresh.setRefreshing(true)
        get(page)

        return view
    }

    fun get(page:Int){
        val retrofit = RetrofitUtil.getInstance()
        var homeList: HomeList? = retrofit.create(HomeList::class.java)
        val subscribe = homeList!!.getHomeList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( Consumer<HomeBean>{
                      it.data.curPage?.let { this.page = it }
                    val adapter = list.adapter
                    if(adapter is ItemRecyclerViewAdapter){
                        it.data.datas!!.let{
                            if(page == 0) {
                                adapter.setValues(it)
                            }else{
                                adapter.appValues(it)
                            }
                        }
                    }
                    if(refresh.isRefreshing()){
                        refresh.setRefreshing(false)
                    }
                }, Consumer<Throwable>{
                     it.printStackTrace()
                    }
                )
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
            mListener =   object :OnListFragmentInteractionListener{
                override fun onListFragmentInteraction(item: Item){
                    Log.i("ss",item.title)
                    Observable.just(item.title).subscribe(System.out::println)
                    val intent = Intent(activity,ContentActivity::class.java)
                    intent.putExtra(this@HomeItemFragment::class.java.simpleName,item)
                    startActivity(intent)
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
