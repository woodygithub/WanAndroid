package com.wan.wanandroid.eyepetizer

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wan.wanandroid.R
import com.wan.wanandroid.eyepetizer.bean.Item
import com.wan.wanandroid.eyepetizer.bean.VideoListBean
import com.wan.wanandroid.eyepetizer.data.EyepetizerRepository

import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_item_list2.view.*
import me.yokeyword.fragmentation.SupportFragment

/**
 * A fragment representing a list of Items.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class KYItemFragment : SupportFragment() ,EyepetizerContract.View{

    override lateinit var presenter: EyepetizerContract.Presenter
    lateinit var list: RecyclerView
    lateinit var refresh: SwipeRefreshLayout
    private lateinit var mListener: OnListFragmentInteractionListener

    override fun render(it: VideoListBean) {
        val adapter = list.adapter
        if(adapter is ItemRecyclerViewAdapter){
            adapter.setValues(it.itemList)
        }
        if(refresh.isRefreshing()){
            refresh.setRefreshing(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = EyepetizerPresenter(EyepetizerRepository(),this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list2, container, false)

        list = view.ky_list
        refresh = view.ky_refresh
        val context = view.getContext()
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = ItemRecyclerViewAdapter(mListener)

        refresh.setOnRefreshListener {
            presenter.load()
        }
        refresh.setOnChildScrollUpCallback { _, child ->
            if(child is RecyclerView){
                var lm = child.layoutManager
                if (lm is LinearLayoutManager){
                    if(lm.findLastVisibleItemPosition() < child.adapter.itemCount -1){
                        if (lm.findFirstVisibleItemPosition() == 0)
                            return@setOnChildScrollUpCallback false
                        return@setOnChildScrollUpCallback true
                    }else{
                        presenter.load()
                        return@setOnChildScrollUpCallback false
                    }
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
        mListener = object:OnListFragmentInteractionListener {
            override fun onListFragmentInteraction(item: Item){
                Log.i("ss",item.type)
                Observable.just(item.type).subscribe(System.out::println)
            }
        }

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
        fun newInstance(columnCount: Int): KYItemFragment {
            val fragment = KYItemFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
