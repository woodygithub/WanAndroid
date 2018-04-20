package com.wan.wanandroid.home

import android.content.Intent
import android.support.v4.app.Fragment
import com.wan.wanandroid.home.bean.HomeBean
import com.wan.wanandroid.home.bean.Item
import com.wan.wanandroid.home.data.WanRepository
import io.reactivex.functions.Consumer

/**
 * Created by Woody on 2018/3/9.
 */
class HomeListPresenter(val wanRepository: WanRepository, val homeView: HomeContract.View): HomeContract.Presenter {
    override fun openDetails(item: Item) {
        when(homeView ){
            is Fragment-> {
                val intent = Intent(homeView.context,ContentActivity::class.java)
                intent.putExtra(item::class.java.simpleName,item)
                homeView.startActivity(intent)
            }
        }
    }

    override fun start() {
        load()
    }

    override fun load(){
        wanRepository.loadSource(Consumer<HomeBean>{
            homeView.render(it.data)
        }, Consumer<Throwable>{
            it.printStackTrace()
        })
    }

}
