package com.wan.wanandroid.eyepetizer.data

import com.wan.wanandroid.eyepetizer.bean.VideoListBean
import io.reactivex.functions.Consumer

interface EyepetizerSource {
    fun loadSource(success: Consumer<VideoListBean>, failed: Consumer<Throwable>)

    fun getSource()
}