package com.wan.wanandroid

import android.app.Activity
import android.view.ViewGroup
import com.just.agentweb.AgentWeb
import com.just.agentweb.MiddlewareWebChromeBase

/**
 * getAgentWeb
 */
fun String.getAgentWeb(
        activity: Activity, webContent: ViewGroup,
        layoutParams: ViewGroup.LayoutParams//,
//        receivedTitleCallback: MiddlewareWebChromeBase?
) = AgentWeb.with(activity)//传入Activity or Fragment
        .setAgentWebParent(webContent, layoutParams)//传入AgentWeb 的父控件
        .useDefaultIndicator()// 使用默认进度条
//        .useMiddlewareWebChrome(receivedTitleCallback) //设置 Web 页面的 title 回调
        .createAgentWeb()//
        .ready()
        .go(this)!!

