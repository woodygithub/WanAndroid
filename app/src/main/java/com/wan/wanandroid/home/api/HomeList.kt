package com.wan.wanandroid.home.api

import com.wan.wanandroid.home.bean.HomeBean
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Woody on 2018/3/6.
 */
open interface HomeList {
    @GET("article/list/{pag}/json")
    fun getHomeList(@Path("pag") pag:Int): Observable<HomeBean>
}