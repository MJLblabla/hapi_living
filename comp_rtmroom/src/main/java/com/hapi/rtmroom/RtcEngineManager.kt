package com.hapi.rtmroom

import android.content.Context
import android.os.Environment
import android.util.Log
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.gl.EglBase
import io.agora.rtc.mediaio.IVideoFrameConsumer
import io.agora.rtc.mediaio.MediaIO
import io.agora.rtc.mediaio.TextureSource
import java.io.File

class RtcEngineManager {

    private var appId = ""
    private lateinit var context: Context
    private var mRtcEngine: RtcEngine? = null

    fun init(context: Context, appId: String) {
        this.context = context
        this.appId = appId
    }


    fun joinChannel(
        channel: String, uid: Int, rtcEventHandler: IRtcEngineEventHandler
        , extra: String = ""
    ): Int {
        try {
            mRtcEngine = RtcEngine.create(context, appId, rtcEventHandler)

            //设置存储日志位置
            val baseDir = Environment.getExternalStorageDirectory().absolutePath + "/com.lucky.live"
            val path = "$baseDir/Agosdk.log"
            val baseDirFile = File(baseDir)
            if (!baseDirFile.exists()) {
                baseDirFile.mkdirs()
            }
            val dirFile = File(path)
            if (!dirFile.exists()) {
                dirFile.createNewFile()
            }
            mRtcEngine?.setLogFile(path)

        } catch (e: Exception) {
            Log.e("rtc", Log.getStackTraceString(e))
        }
        mRtcEngine?.enableVideo()
        return mRtcEngine?.joinChannel(null, channel, extra, uid) ?: 0
    }


    fun leaveChannel() {
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }


    var source: MyTextureSource? = null
        private set

    fun resetEngineSource(w:Int,h:Int) {
        source = MyTextureSource(null, w, h)
        (source as MyTextureSource).setPixelFormat(MediaIO.PixelFormat.RGBA)
        mRtcEngine?.setVideoSource(source)
        mRtcEngine?.enableLocalAudio(true)
        mRtcEngine?.enableLocalVideo(true)
    }


    fun getLazyRtcEngine():RtcEngine{
        return mRtcEngine!!
    }

    class MyTextureSource(sharedContext: EglBase.Context?, width: Int, height: Int) : TextureSource(sharedContext, width, height) {

        var mIVideoFrameConsumer: IVideoFrameConsumer? = null
            private set
        var mVideoFrameConsumerReady = false
            private set


        override fun onCapturerOpened(): Boolean {
            mIVideoFrameConsumer = mConsumer.get()
            return true
        }

        override fun onCapturerStarted(): Boolean {
            mVideoFrameConsumerReady = true
            return true
        }

        override fun onCapturerStopped() {
            mVideoFrameConsumerReady = false
        }

        override fun onCapturerClosed() {
            mVideoFrameConsumerReady = false
        }

        fun setWidth(width: Int) {
            this.mWidth = width
        }

        fun setHeight(height: Int) {
            this.mHeight = height
        }

        override fun getBufferType(): Int {
            // return super.getBufferType();
            return MediaIO.BufferType.BYTE_ARRAY.intValue()
        }

        fun setPixelFormat(pixelFormat: MediaIO.PixelFormat) {
            this.mPixelFormat = pixelFormat.intValue()
        }
    }
}