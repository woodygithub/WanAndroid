package com.wan.wanandroid.eyepetizer

import com.wan.wanandroid.eyepetizer.api.KYApi
import com.wan.wanandroid.eyepetizer.bean.VideoListBean
import com.wan.wanandroid.eyepetizer.data.EyepetizerRepository
import com.wan.wanandroid.retrofit.RetrofitUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class EyepetizerPresenter(val eyeRepository: EyepetizerRepository, val eyeView: EyepetizerContract.View): EyepetizerContract.Presenter {

    override fun start() {
        load()
    }

    override fun load() {
        val retrofit = RetrofitUtil.getInstance()
        var kyapi = retrofit.create(KYApi::class.java)
        kyapi.getVideoList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer<VideoListBean>{
                    eyeView.render(it)
                }, Consumer<Throwable> {
                    it.printStackTrace()
                })
    }

}