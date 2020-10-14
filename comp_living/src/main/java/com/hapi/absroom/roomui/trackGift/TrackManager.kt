package com.hapi.asbroom.roomui.trackGift

import android.util.Log
import com.hapi.absroom.live.LivingRoomManager
import com.hapi.asbroom.RoomSession
import com.hapi.asbroom.RoomStatusMonitor
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * 轨道控制
 */
class TrackManager<T> : RoomStatusMonitor {

    var trackSpan = 500L
    private val trackViews = ArrayList<TrackView<T>>()
    private val trackModeQueue = LinkedList<T>()

    fun init() {
        LivingRoomManager.addMonitor(this)
    }
    /**
     * 礼物轨道view
     * 把ui上轨道view attach上来
     */
    fun addTrackView(trackView: TrackView<T>) {
        trackViews.add(trackView)
    }

    private var job: Job?=null

    private fun newJob(){
        job= GlobalScope.launch(Dispatchers.Main, start = CoroutineStart.LAZY) {
            var next = true

            while (next){
                var giftTrackMode=trackModeQueue.peek()
                var deal = false
                if(giftTrackMode!=null){
                    trackViews.forEach {
                        if (it.isShow()) {
                            //如果在处理同一个礼物
                            if (it.showInSameTrack(giftTrackMode)) {
                                it.onNewModel(giftTrackMode)
                                deal = true
                                Log.d("TrackManager","在处理同一个礼物")
                                return@forEach
                            }

                        }
                    }


                    //是否有空闲的轨道
                    if (!deal) {
                        trackViews.forEach {
                            if (!deal&&!it.isShow()) {
                                it.onNewModel(giftTrackMode)
                                deal = true
                                Log.d("TrackManager","空闲礼物")
                                return@forEach
                            }
                        }

                    }

                    if(!deal){
                        Log.d("TrackManager","  trackModeQueue.add(gi ${  trackModeQueue.size}")
                       delay(trackSpan)
                    }else{
                        trackModeQueue.pop()
                        if(trackModeQueue.isEmpty()){
                            next = false
                            job = null
                        }else{
                            delay(trackSpan)
                        }
                    }

                }else{
                    next = false
                    job = null
                }
            }

        }
    }
    /**
     * 忘轨道上添加新礼物
     */


    fun onNewTrackArrive(giftTrackMode: T) {

        Log.d("TrackManager","onNewTrackArrive")
        trackModeQueue.add(giftTrackMode)

        if(job==null){
            newJob()
            job?.start()
        }

    }

    /**
     * 仅仅通知房间切换 (((((并没有加入房间成功的意思
     */
    override fun onRoomSessionChange(roomSession: RoomSession) {
        trackViews.forEach {
            it.clear(true)
        }
        trackModeQueue.clear()
    }

    fun resetView(){
        trackViews.forEach {
            it.clear()
        }
        trackViews.clear()
        trackModeQueue.clear()
    }
    /**
     * 关闭房间
     */
    override fun onCloseRoom() {
        trackViews.forEach {
            it.clear()
        }
        job?.cancel()
        job=null
        LivingRoomManager.removeMonitor(this)
    }

    override fun onMinimizeWindow() {
        trackViews.forEach {
            it.clear()
        }
        job?.cancel()
        job=null
        trackModeQueue.clear()
        trackViews.clear()
        LivingRoomManager.removeMonitor(this)
    }


}