package com.wan.wanandroid.eyepetizer.bean

/**
 * Created by Woody on 2018/3/21.
 */
data class VideoListBean(val count: Int,
                         val total: Int,
                         val nextPageUrl:String,
                         val adExist: Boolean,
                         val date: Long,
                         val nextPublishTime: Long,
                         val dialog: String,
                         val topIssue: String,
                         val refreshCount: Int,
                         val lastStartId: Int,
                         val itemList: MutableList<Item>)

data class Item(val type: String, val data:DataBean)

data class DataBean(val dataType: String,
                    val id: Int,
                    val title: String,
                    val description: String,
                    val provider: ProviderBean,
                    val category: String,
                    val cover: CoverBean,
                    val playUrl: String,
                    val duration: Int,
                    val webUrl: WebUrlBean,
                    val releaseTime: Long,
                    val consumption:ConsumptionBean,
                    val type: String,
                    val idx: Int,
                    val date: Long,
                    val collected: Boolean,
                    val played: Boolean,
                    val playInfo: MutableList<PlayInfoBean>,
                    val tags: MutableList<Tags>)

data class ProviderBean(val name: String, val alias: String, val icon: String)

data class CoverBean(val feed: String, val detail: String, val blurred: String)

data class WebUrlBean(val raw: String, val forWeibo: String)

data class ConsumptionBean(val collectionCount: Int, val shareCount: Int, val replyCount: Int)

data class PlayInfoBean(val height: Int, val width: Int, val name: String, val type: String, val url: String, val urlList: MutableList<UrlListBean>)

data class UrlListBean(val name: String, val url: String)

data class Tags(val id: Int, val name: String, val actionUrl: String)

data class RealUrl(val url:String)