package com.wan.wanandroid.home.data

import com.wan.wanandroid.home.bean.HomeBean
import io.reactivex.functions.Consumer

interface WanDataSource {
    fun loadSource(success: Consumer<HomeBean>, failed: Consumer<Throwable>)

    fun getSource()
}