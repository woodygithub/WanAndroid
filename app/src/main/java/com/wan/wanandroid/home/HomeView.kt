package com.wan.wanandroid.home

import android.support.v7.widget.RecyclerView
import com.hannesdorfmann.mosby3.mvp.MvpView
import com.jakewharton.rxbinding2.InitialValueObservable
import com.wan.wanandroid.home.state.HomeListState
import io.reactivex.Observable

/**
 * Created by Woody on 2018/3/8.
 */
interface HomeView {

    /**
     * 渲染
     */
    fun render(homeListState: HomeListState)

}