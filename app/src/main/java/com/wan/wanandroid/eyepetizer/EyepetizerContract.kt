package com.wan.wanandroid.eyepetizer

import com.wan.wanandroid.BasePresenter
import com.wan.wanandroid.BaseView
import com.wan.wanandroid.eyepetizer.bean.VideoListBean

interface EyepetizerContract {

    interface View: BaseView<Presenter> {
        fun render(it: VideoListBean)
    }

    interface Presenter : BasePresenter {

        fun load()

    }
}