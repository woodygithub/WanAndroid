package com.wan.wanandroid.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import com.wan.wanandroid.R
import com.wan.wanandroid.eyepetizer.KYItemFragment
import com.wan.wanandroid.home.HomeItemFragment
import kotlinx.android.synthetic.main.activity_main.*
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.SupportActivity

class MainActivity : SupportActivity() {

    lateinit var f:Array<ISupportFragment>

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                showHideFragment(f[0])  // 加载根Fragment
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                showHideFragment(f[1])  // 加载根Fragment
                return@OnNavigationItemSelectedListener true
            }
//            R.id.navigation_notifications -> {
////                message.setText(R.string.title_notifications)
//                return@OnNavigationItemSelectedListener true
//            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (findFragment(HomeItemFragment::class.java) == null) {
            f = arrayOf(HomeItemFragment.newInstance(1), KYItemFragment.newInstance(1))
            loadMultipleRootFragment(R.id.fragment, 1, *f)  // 加载根Fragment
        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.setSelectedItemId(R.id.navigation_home)
    }
}
