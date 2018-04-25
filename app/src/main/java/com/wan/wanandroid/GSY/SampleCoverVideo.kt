package com.wan.wanandroid.GSY

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.wan.wanandroid.R
import kotlinx.android.synthetic.main.video_layout_cover.view.*

/**
 * 带封面
 * Created by guoshuyu on 2017/9/3.
 */

class SampleCoverVideo : StandardGSYVideoPlayer {

    internal lateinit var mCoverImage: ImageView

    internal lateinit var mCoverOriginUrl: String

    internal var mDefaultRes: Int = 0

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag!!) {}

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun init(context: Context) {
        super.init(context)
        mCoverImage = thumbImage

        if (mThumbImageViewLayout != null && (mCurrentState == -1 || mCurrentState == GSYVideoView.CURRENT_STATE_NORMAL || mCurrentState == GSYVideoView.CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.visibility = View.VISIBLE
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.video_layout_cover
    }

    override fun onAutoCompletion() {
        startPlayLogic()
    }

    fun loadCoverImage(url: String, res: Int) {
        mCoverOriginUrl = url
        mDefaultRes = res
        Glide.with(context.applicationContext)
                .setDefaultRequestOptions(
                        RequestOptions()
                                .frame(1000000)
                                .centerCrop()
                                .error(res)
                                .placeholder(res))
                .load(url)
                .into(mCoverImage)
    }

    override fun startWindowFullscreen(context: Context, actionBar: Boolean, statusBar: Boolean): GSYBaseVideoPlayer {
        val gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar)
        val sampleCoverVideo = gsyBaseVideoPlayer as SampleCoverVideo
        sampleCoverVideo.loadCoverImage(mCoverOriginUrl, mDefaultRes)
        return gsyBaseVideoPlayer
    }
}
