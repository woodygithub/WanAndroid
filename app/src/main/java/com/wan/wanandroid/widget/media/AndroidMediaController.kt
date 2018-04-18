/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wan.wanandroid.widget.media

import android.content.Context
import android.support.v7.app.ActionBar
import android.util.AttributeSet
import android.view.View
import android.widget.MediaController

import java.util.ArrayList

class AndroidMediaController : MediaController, IMediaController {
    private var mActionBar: ActionBar? = null

    //----------
    // Extends
    //----------
    private val mShowOnceArray = ArrayList<View>()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, useFastForward: Boolean) : super(context, useFastForward) {
        initView(context)
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    private fun initView(context: Context) {}

    fun setSupportActionBar(actionBar: ActionBar?) {
        mActionBar = actionBar
        if (isShowing()) {
            actionBar!!.show()
        } else {
            actionBar!!.hide()
        }
    }

    override fun show() {
        super.show()
        if (mActionBar != null)
            mActionBar!!.show()
    }

    override fun hide() {
        super.hide()
        if (mActionBar != null)
            mActionBar!!.hide()
        for (view in mShowOnceArray)
            view.visibility = View.GONE
        mShowOnceArray.clear()
    }

    override fun showOnce(view: View) {
        mShowOnceArray.add(view)
        view.visibility = View.VISIBLE
        show()
    }
}
