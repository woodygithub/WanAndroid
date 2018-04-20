package com.wan.wanandroid.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultChromeClient
import com.just.agentweb.MiddlewareWebChromeBase
import com.wan.wanandroid.R
import com.wan.wanandroid.getAgentWeb
import com.wan.wanandroid.home.bean.Item
import com.wan.wanandroid.login.LoginActivity
import kotlinx.android.synthetic.main.activity_content.*

class ContentActivity: AppCompatActivity() {
    private lateinit var agentWeb: AgentWeb
    private lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        toolbar.run {
            title = ""
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.setNavigationOnClickListener{
                if(!agentWeb.back()){
                    finish()
                }
            }
        }
        intent.extras?.let {
            item = it.getParcelable<Item>(Item::class.java.simpleName)
            agentWeb = item.link.getAgentWeb(
                    this,
                    webContent,
                    LinearLayout.LayoutParams(-1, -1)
            )
        }
    }

    override fun onPause() {
        agentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onResume() {
        agentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        agentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (agentWeb.handleKeyEvent(keyCode, event)) {
            true
        } else {
            finish()
            super.onKeyDown(keyCode, event)
        }
    }
}