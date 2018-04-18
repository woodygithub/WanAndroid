package com.wan.wanandroid.eyepetizer.api

import com.wan.wanandroid.eyepetizer.bean.RealUrl
import com.wan.wanandroid.eyepetizer.bean.VideoListBean
import com.wan.wanandroid.retrofit.annotate.Gson
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Created by Woody on 2018/3/21.
 */

open interface KYApi {
    @GET("https://baobab.kaiyanapp.com/api/v4/tabs/selected")
    @Gson
    fun getVideoList(): Observable<VideoListBean>

    @GET
    @com.wan.wanandroid.retrofit.annotate.String
    fun getVideoPath(@Url url: String): Observable<RealUrl>
}