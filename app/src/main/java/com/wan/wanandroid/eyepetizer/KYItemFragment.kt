package com.wan.wanandroid.eyepetizer

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.wan.wanandroid.GSY.SampleCoverVideo
import com.wan.wanandroid.R
import com.wan.wanandroid.eyepetizer.bean.Item
import com.wan.wanandroid.eyepetizer.bean.VideoListBean
import com.wan.wanandroid.eyepetizer.data.EyepetizerRepository
import com.wan.wanandroid.eyepetizer.state.EyepetizerState

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
    lateinit var linearLM: LinearLayoutManager
    lateinit var refresh: SwipeRefreshLayout
    private lateinit var mListener: OnListFragmentInteractionListener
    lateinit var scrollCalculatorHelper: ScrollCalculatorHelper

    override fun render(it: VideoListBean) {
        val adapter = list.adapter
        if(adapter is ItemRecyclerViewAdapter){
            if(EyepetizerState.nextPageUrl == null){
                adapter.setValues(it.itemList)

            }else{
                adapter.appValues(it.itemList)
            }
        }
        EyepetizerState.nextPageUrl = it.nextPageUrl
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
        linearLM =LinearLayoutManager(context)
        list.layoutManager = linearLM
        list.adapter = ItemRecyclerViewAdapter(mListener)
        PagerSnapHelper().attachToRecyclerView(list)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()// 创建了一张白纸
        windowManager.defaultDisplay.getMetrics(outMetrics)// 给白纸设置宽高
        scrollCalculatorHelper = ScrollCalculatorHelper(R.id.video_item_player,0, outMetrics.heightPixels)
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            internal var firstVisibleItem: Int = 0
            internal var lastVisibleItem:Int = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollCalculatorHelper.onScrollStateChanged(recyclerView,newState)
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                firstVisibleItem = linearLM.findFirstVisibleItemPosition()
                lastVisibleItem = linearLM.findLastVisibleItemPosition()
                scrollCalculatorHelper.onScroll(recyclerView, firstVisibleItem, lastVisibleItem, 1)
            }
        })
        refresh.setOnRefreshListener {
            EyepetizerState.nextPageUrl = null
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
        refresh.setRefreshing(true)
        presenter.start()
        return view
    }

    override fun onBackPressedSupport(): Boolean {
        if (GSYVideoManager.backFromWindowFull(context)) {
            return true
        }
        return super.onBackPressedSupport()
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onSupportVisible() {
        super.onSupportVisible()

//        linearLM.getChildAt(linearLM.findFirstCompletelyVisibleItemPosition())?.let {
//            it.findViewById<SampleCoverVideo>(R.id.video_item_player).startPlayLogic()
//        }
    }
    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
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
