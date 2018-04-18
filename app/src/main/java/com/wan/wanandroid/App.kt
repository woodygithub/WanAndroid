package com.wan.wanandroid

import android.app.Application
import me.yokeyword.fragmentation.Fragmentation

/**
 * Created by Woody on 2018/3/6.
 */
class App: Application(){
    override fun onCreate() {
        super.onCreate()
        Fragmentation.builder()
                // 显示悬浮球 ; 其他Mode:SHAKE: 摇一摇唤出   NONE：隐藏
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG)
                 // 更多查看wiki或demo
                .install()


    }
}