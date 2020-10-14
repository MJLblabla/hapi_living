package com.hapi.asbroom.roomui.bigGift

import android.view.ViewGroup
import com.hapi.absroom.live.LivingRoomManager
import com.hapi.asbroom.RoomSession
import com.hapi.asbroom.RoomStatusMonitor
import java.util.*
import kotlin.collections.ArrayList

class BigGiftManager<T>: RoomStatusMonitor {

   fun init() {
       queueBigGiftView.clear()
       giftTrackModeQueue.clear()
        LivingRoomManager.addMonitor(this)
    }
    /**
     * 大礼物容器
     */
    private var bigContainer: ViewGroup? = null

    private var queueBigGiftView = ArrayList<BigGiftView<T>>()

    private val giftTrackModeQueue = LinkedList<T>()
    /**
     * @param bigContainer 大礼物容器
     * @param  queueBigGiftView 用于排队播放的大礼物 建议一个或者两个
     */
    fun attch(bigContainer: ViewGroup, queueBigGiftView: ArrayList<BigGiftView<T>>) {
        this.bigContainer = bigContainer
        this.queueBigGiftView.addAll(queueBigGiftView)
        queueBigGiftView.forEach {
            it.finishedCall = {
                val head =
                    giftTrackModeQueue.peek()
                if(head!=null){
                    var deal = false
                    queueBigGiftView.forEach { v ->
                        if (v.playIfPlayAble(head)) {
                            deal = true
                            return@forEach
                        }
                    }
                    if (deal) {
                        giftTrackModeQueue.poll()
                    }
                }
            }
        }
    }

    /**
     * 排队播放的大动画
     */
    fun playInQueen(bigAnimalMode: T) {
        var deal = false
        queueBigGiftView?.forEach {
            if (it.playIfPlayAble(bigAnimalMode)) {
                deal = true
                return@forEach
            }
        }
        if (!deal) {
            giftTrackModeQueue.add(bigAnimalMode)
        }
    }

    /**
     * 不排队马上播放
     * newBigGiftView :构造一个新BigGiftView 我来控制播放
     */
    fun playNow(bigAnimalMode: T, newBigGiftView: BigGiftView<T>) {
        bigContainer?.addView(newBigGiftView.getView())
        newBigGiftView.finishedCall = {
            bigContainer?.removeView(newBigGiftView.getView())
        }

    }

    /**
     * 仅仅通知房间切换 (((((并没有加入房间成功的意思
     */
    override fun onRoomSessionChange(roomSession: RoomSession) {
    }

    /**
     * 关闭房间
     */
    override fun onCloseRoom() {
        LivingRoomManager.removeMonitor(this)
        resetView()
    }

    fun resetView(){
        queueBigGiftView.forEach {
            it.clear()
        }
        bigContainer=null
        queueBigGiftView.clear()
        giftTrackModeQueue.clear()
    }

    override fun onMinimizeWindow() {
        super.onMinimizeWindow()
        onCloseRoom()
    }
}