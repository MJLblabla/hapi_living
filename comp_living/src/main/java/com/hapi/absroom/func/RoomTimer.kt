package com.hapi.absroom.func

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import com.hapi.absroom.live.LivingRoomManager
import com.hapi.asbroom.RoomSession
import com.hapi.asbroom.RoomStatusMonitor

/**
 * 房间计时器
 */
object RoomTimer:RoomStatusMonitor {

    var millis = 0
    private val WHAT = 102
    private var isStart = false
    fun resetTime(time: Int) {
        this.millis = time
    }

    fun init(){
        Log.d("mmm",LivingRoomManager.toString())
        LivingRoomManager.addMonitor(this)
    }

    private var handler = @SuppressLint("HandlerLeak")

    object : Handler() {
        override fun handleMessage(msg: Message) {
            if(!isStart){
                return
            }
            when (msg.what) {
                WHAT -> {
                    listeners.forEach {
                        it.onTick(millis)
                    }
                    millis++
                    sendEmptyMessageDelayed(WHAT, 1000)
                }
            }
        }
    }





    //自动启动 不需要手动控制
    private fun autoStart() {
        millis = 0
        isStart = true
        handler.sendEmptyMessage(WHAT)
    }

    private fun destroy() {
        millis = 0
         stop()
        listeners.clear()

    }

    private fun stop(){
        isStart = false
        handler.removeMessages(WHAT)
    }

    val listeners = ArrayList<TimeCountListener>()

    interface TimeCountListener {
        fun onTick(millis: Int)
    }

    override fun onRoomSessionChange(roomSession: RoomSession) {

    }

    override fun onRoomLevel(roomSession: RoomSession) {
        super.onRoomLevel(roomSession)
        stop()
    }

    override fun onRoomChecked(roomSession: RoomSession) {
        super.onRoomChecked(roomSession)
        autoStart()
    }

    override fun onCloseRoom() {
        destroy()
        LivingRoomManager.removeMonitor(this)
    }


}