package com.wan.wanandroid.eyepetizer

import com.wan.wanandroid.eyepetizer.bean.VideoListBean
import com.wan.wanandroid.eyepetizer.data.EyepetizerRepository
import io.reactivex.functions.Consumer

class EyepetizerPresenter(val eyeRepository: EyepetizerRepository, val eyeView: EyepetizerContract.View): EyepetizerContract.Presenter {

    override fun start() {
        load()
    }

    override fun load() {
        eyeRepository.loadSource(Consumer<VideoListBean>{
            eyeView.render(it)
        }, Consumer<Throwable> {
            it.printStackTrace()
        })
    }
}