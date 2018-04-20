package com.wan.wanandroid.eyepetizer.data

import com.wan.wanandroid.eyepetizer.api.KYApi
import com.wan.wanandroid.eyepetizer.bean.VideoListBean
import com.wan.wanandroid.retrofit.RetrofitUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class EyepetizerRepository: EyepetizerSource {
    override fun loadSource(success: Consumer<VideoListBean>, failed: Consumer<Throwable>) {
        val retrofit = RetrofitUtil.getInstance()
        val kyapi = retrofit.create(KYApi::class.java)
        kyapi.getVideoList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success,failed)
    }
    override fun getSource() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}