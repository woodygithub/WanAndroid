package com.wan.wanandroid.eyepetizer

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.wan.wanandroid.GSY.SampleCoverVideo
import com.wan.wanandroid.R
import com.wan.wanandroid.eyepetizer.bean.Item
import com.wan.wanandroid.eyepetizer.KYItemFragment.OnListFragmentInteractionListener
import com.wan.wanandroid.eyepetizer.api.KYApi
import com.wan.wanandroid.eyepetizer.bean.RealUrl
import com.wan.wanandroid.retrofit.RetrofitUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_item2.view.*
import kotlinx.android.synthetic.main.video_layout_cover.view.*

/**
 * [RecyclerView.Adapter] that can display a [Item] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class ItemRecyclerViewAdapter( private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    private var mValues:MutableList<Item> = ArrayList<Item>()

    fun setValues(values: MutableList<Item>){
        mValues = values
        notifyDataSetChanged()
    }

    fun appValues(values: List<Item>){
        mValues.addAll(values)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mTitleView.text = mValues[position].data.title
        holder.mDescriptionView.text = mValues[position].data.description

        mValues[position].data.cover?.let {
            Glide.with(holder.mImgView.context).load(it.detail).into(holder.mImgView)
        }
        mValues[position].data.playUrl?.let {
            RetrofitUtil.getInstance().create(KYApi::class.java)
                    .getVideoPath(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer<RealUrl>{
                        Log.e("url",it.url)
                        //initVideoView(holder.mPlayer,it.url)
                        holder.gsyVideoOptionBuilder
                                .setIsTouchWiget(true)
                                .setSetUpLazy(true)//lazy可以防止滑动卡顿
                                .setUrl(it.url)
                                .setVideoTitle(mValues[position].data.title)
                                .setCacheWithPlay(true)
                                .setRotateViewAuto(true)
                                .setLockLand(true)
                                .setPlayTag("1111111")
                                .setShowFullAnimation(true)
                                .setNeedLockFull(true)
                                .setPlayPosition(position)
                                .setVideoAllCallBack(object : GSYSampleCallBack() {
                                    override fun onPrepared(url: String?, vararg objects: Any?) {
                                        super.onPrepared(url, *objects)
                                        if (!holder.mPlayer.isIfCurrentIsFullscreen()) {
                                            //静音
                                            GSYVideoManager.instance().isNeedMute = true
                                        }
                                    }

                                    override fun onQuitFullscreen(url: String?, vararg objects: Any) {
                                        super.onQuitFullscreen(url, *objects)
                                        //全屏不静音
                                        GSYVideoManager.instance().isNeedMute = true
                                    }

                                    override fun onEnterFullscreen(url: String?, vararg objects: Any) {
                                        super.onEnterFullscreen(url, *objects)
                                        GSYVideoManager.instance().isNeedMute = false
                                        holder.mPlayer.getCurrentPlayer().getTitleTextView().setText(objects[0] as String)
                                    }
                                }).build(holder.mPlayer)

                        //增加title
                        holder.mPlayer.getTitleTextView().setVisibility(View.GONE)

                        //设置返回键
                        holder.mPlayer.getBackButton().setVisibility(View.GONE)

                        holder.mPlayer.getFullscreenButton().setOnClickListener({
                                holder.mPlayer.startWindowFullscreen(holder.mPlayer.context,true,true)
                        });

                    }, Consumer<Throwable> {
                        it.printStackTrace()
                    })
        }


        holder.mView.setOnClickListener {
            mListener?.onListFragmentInteraction(holder.mItem!!)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mTitleView: TextView
        val mDescriptionView: TextView
        val mImgView: ImageView
        val mPlayer: SampleCoverVideo
        var mItem: Item? = null
        val gsyVideoOptionBuilder: GSYVideoOptionBuilder

        init {
            mTitleView = mView.title
            mDescriptionView = mView.description
            mImgView = mView.thumbImage
            mPlayer = mView.video_item_player
            gsyVideoOptionBuilder = GSYVideoOptionBuilder()
        }

        override fun toString(): String {
            return super.toString() + " '" + mDescriptionView.text + "'"
        }
    }
}
