package com.wan.wanandroid.home.data

import com.wan.wanandroid.home.api.HomeList
import com.wan.wanandroid.home.bean.HomeBean
import com.wan.wanandroid.home.state.HomeListState
import com.wan.wanandroid.retrofit.RetrofitUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class WanRepository: WanDataSource {
    override fun loadSource(success: Consumer<HomeBean>,failed: Consumer<Throwable>) {
        val retrofit = RetrofitUtil.getInstance()
        var homeList: HomeList? = retrofit.create(HomeList::class.java)
        val subscribe = homeList!!.getHomeList(HomeListState.page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( success, failed)
    }

    override fun getSource() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}