package com.wan.wanandroid.home

import com.wan.wanandroid.BasePresenter
import com.wan.wanandroid.BaseView
import com.wan.wanandroid.home.bean.Data
import com.wan.wanandroid.home.bean.Item

interface HomeContract {

    interface View: BaseView<Presenter> {
        fun render(it: Data)
    }
    interface Presenter : BasePresenter {

        fun load()

        fun openDetails(requestedTask: Item)

    }
}