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

import android.annotation.TargetApi
import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.TextureView
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.ISurfaceTextureHolder
import tv.danmaku.ijk.media.player.ISurfaceTextureHost
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class TextureRenderView : TextureView, IRenderView {
    private var mMeasureHelper: MeasureHelper? = null

    override fun getView(): View {
        return this
    }

    //--------------------
    // TextureViewHolder
    //--------------------

    fun getSurfaceHolder(): IRenderView.ISurfaceHolder{
        return InternalSurfaceHolder(this, mSurfaceCallback?.mSurfaceTexture!!, mSurfaceCallback!!)
    }

    private var mSurfaceCallback: SurfaceCallback? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    private fun initView(context: Context) {
        mMeasureHelper = MeasureHelper(this)
        mSurfaceCallback = SurfaceCallback(this)
        surfaceTextureListener = mSurfaceCallback
    }

    override fun shouldWaitForResize(): Boolean {
        return false
    }

    override fun onDetachedFromWindow() {
        mSurfaceCallback!!.willDetachFromWindow()
        super.onDetachedFromWindow()
        mSurfaceCallback!!.didDetachFromWindow()
    }

    //--------------------
    // Layout & Measure
    //--------------------
    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper!!.setVideoSize(videoWidth, videoHeight)
            requestLayout()
        }
    }

    override fun setVideoSampleAspectRatio(videoSarNum: Int, videoSarDen: Int) {
        if (videoSarNum > 0 && videoSarDen > 0) {
            mMeasureHelper!!.setVideoSampleAspectRatio(videoSarNum, videoSarDen)
            requestLayout()
        }
    }

    override fun setVideoRotation(degree: Int) {
        mMeasureHelper!!.setVideoRotation(degree)
        rotation = degree.toFloat()
    }

    override fun setAspectRatio(aspectRatio: Int) {
        mMeasureHelper!!.setAspectRatio(aspectRatio)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mMeasureHelper!!.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mMeasureHelper!!.measuredWidth, mMeasureHelper!!.measuredHeight)
    }

    private class InternalSurfaceHolder(private val mTextureView: TextureRenderView,
                                        private val surfaceTexture: SurfaceTexture?,
                                        private val mSurfaceTextureHost: ISurfaceTextureHost) : IRenderView.ISurfaceHolder {
        override fun getSurfaceTexture(): SurfaceTexture? {
            return surfaceTexture
        }

        override fun getRenderView(): IRenderView{
            return mTextureView
        }

        override fun getSurfaceHolder(): SurfaceHolder?{
            return null
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun bindToMediaPlayer(mp: IMediaPlayer) {
            if (mp == null)
                return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && mp is ISurfaceTextureHolder) {
                val textureHolder = mp as ISurfaceTextureHolder
                mTextureView.mSurfaceCallback!!.setOwnSurfaceTexture(false)

                val surfaceTexture = textureHolder.surfaceTexture
                if (surfaceTexture != null) {
                    mTextureView.surfaceTexture = surfaceTexture
                } else {
                    textureHolder.surfaceTexture = this.surfaceTexture
                    textureHolder.setSurfaceTextureHost(mTextureView.mSurfaceCallback)
                }
            } else {
                mp.setSurface(openSurface())
            }
        }

        override fun openSurface(): Surface? {
            return if (surfaceTexture == null) null else Surface(surfaceTexture)
        }
    }

    //-------------------------
    // SurfaceHolder.Callback
    //-------------------------

    override fun addRenderCallback(callback: IRenderView.IRenderCallback) {
        mSurfaceCallback!!.addRenderCallback(callback)
    }

    override fun removeRenderCallback(callback: IRenderView.IRenderCallback) {
        mSurfaceCallback!!.removeRenderCallback(callback)
    }

    private class SurfaceCallback(renderView: TextureRenderView) : TextureView.SurfaceTextureListener, ISurfaceTextureHost {
        internal var mSurfaceTexture: SurfaceTexture? = null
        private var mIsFormatChanged: Boolean = false
        private var mWidth: Int = 0
        private var mHeight: Int = 0

        private var mOwnSurfaceTexture = true
        private var mWillDetachFromWindow = false
        private var mDidDetachFromWindow = false

        private val mWeakRenderView: WeakReference<TextureRenderView>
        private val mRenderCallbackMap = ConcurrentHashMap<IRenderView.IRenderCallback, Any>()

        init {
            mWeakRenderView = WeakReference(renderView)
        }

        fun setOwnSurfaceTexture(ownSurfaceTexture: Boolean) {
            mOwnSurfaceTexture = ownSurfaceTexture
        }

        fun addRenderCallback(callback: IRenderView.IRenderCallback) {
            mRenderCallbackMap[callback] = callback

            var surfaceHolder: IRenderView.ISurfaceHolder? = null
            if (mSurfaceTexture != null) {
                if (surfaceHolder == null)
                    surfaceHolder = InternalSurfaceHolder(mWeakRenderView.get()!!, mSurfaceTexture, this)
                callback.onSurfaceCreated(surfaceHolder, mWidth, mHeight)
            }

            if (mIsFormatChanged) {
                if (surfaceHolder == null)
                    surfaceHolder = InternalSurfaceHolder(mWeakRenderView.get()!!, mSurfaceTexture, this)
                callback.onSurfaceChanged(surfaceHolder, 0, mWidth, mHeight)
            }
        }

        fun removeRenderCallback(callback: IRenderView.IRenderCallback) {
            mRenderCallbackMap.remove(callback)
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            mSurfaceTexture = surface
            mIsFormatChanged = false
            mWidth = 0
            mHeight = 0

            val surfaceHolder = InternalSurfaceHolder(mWeakRenderView.get()!!, surface, this)
            for (renderCallback in mRenderCallbackMap.keys) {
                renderCallback.onSurfaceCreated(surfaceHolder, 0, 0)
            }
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            mSurfaceTexture = surface
            mIsFormatChanged = true
            mWidth = width
            mHeight = height

            val surfaceHolder = InternalSurfaceHolder(mWeakRenderView.get()!!, surface, this)
            for (renderCallback in mRenderCallbackMap.keys) {
                renderCallback.onSurfaceChanged(surfaceHolder, 0, width, height)
            }
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            mSurfaceTexture = surface
            mIsFormatChanged = false
            mWidth = 0
            mHeight = 0

            val surfaceHolder = InternalSurfaceHolder(mWeakRenderView.get()!!, surface, this)
            for (renderCallback in mRenderCallbackMap.keys) {
                renderCallback.onSurfaceDestroyed(surfaceHolder)
            }

            Log.d(TAG, "onSurfaceTextureDestroyed: destroy: $mOwnSurfaceTexture")
            return mOwnSurfaceTexture
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

        //-------------------------
        // ISurfaceTextureHost
        //-------------------------

        override fun releaseSurfaceTexture(surfaceTexture: SurfaceTexture?) {
            if (surfaceTexture == null) {
                Log.d(TAG, "releaseSurfaceTexture: null")
            } else if (mDidDetachFromWindow) {
                if (surfaceTexture !== mSurfaceTexture) {
                    Log.d(TAG, "releaseSurfaceTexture: didDetachFromWindow(): release different SurfaceTexture")
                    surfaceTexture.release()
                } else if (!mOwnSurfaceTexture) {
                    Log.d(TAG, "releaseSurfaceTexture: didDetachFromWindow(): release detached SurfaceTexture")
                    surfaceTexture.release()
                } else {
                    Log.d(TAG, "releaseSurfaceTexture: didDetachFromWindow(): already released by TextureView")
                }
            } else if (mWillDetachFromWindow) {
                if (surfaceTexture !== mSurfaceTexture) {
                    Log.d(TAG, "releaseSurfaceTexture: willDetachFromWindow(): release different SurfaceTexture")
                    surfaceTexture.release()
                } else if (!mOwnSurfaceTexture) {
                    Log.d(TAG, "releaseSurfaceTexture: willDetachFromWindow(): re-attach SurfaceTexture to TextureView")
                    setOwnSurfaceTexture(true)
                } else {
                    Log.d(TAG, "releaseSurfaceTexture: willDetachFromWindow(): will released by TextureView")
                }
            } else {
                if (surfaceTexture !== mSurfaceTexture) {
                    Log.d(TAG, "releaseSurfaceTexture: alive: release different SurfaceTexture")
                    surfaceTexture.release()
                } else if (!mOwnSurfaceTexture) {
                    Log.d(TAG, "releaseSurfaceTexture: alive: re-attach SurfaceTexture to TextureView")
                    setOwnSurfaceTexture(true)
                } else {
                    Log.d(TAG, "releaseSurfaceTexture: alive: will released by TextureView")
                }
            }
        }

        fun willDetachFromWindow() {
            Log.d(TAG, "willDetachFromWindow()")
            mWillDetachFromWindow = true
        }

        fun didDetachFromWindow() {
            Log.d(TAG, "didDetachFromWindow()")
            mDidDetachFromWindow = true
        }
    }

    //--------------------
    // Accessibility
    //--------------------

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.className = TextureRenderView::class.java.name
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = TextureRenderView::class.java.name
    }

    companion object {
        private val TAG = "TextureRenderView"
    }
}
