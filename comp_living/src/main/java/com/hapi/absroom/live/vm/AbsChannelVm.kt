package com.hapi.asbroom.audiolive

import android.app.Application
import android.os.Bundle
import com.hapi.absroom.live.LivingRoomManager
import com.hapi.asbroom.RoomSession
import com.hapi.asbroom.RoomStatusMonitor
import com.hipi.vm.BaseViewModel
import com.pince.im.ImMsgDispatcher
import com.pince.im.parser.ImActionMsgListener
import com.pince.im.parser.ImGroupActionMsgListener

abstract class AbsChannelVm(application: Application, bundle: Bundle?) :
    BaseViewModel(application), RoomStatusMonitor {

    protected val channeMsgMonitor = ImGroupActionMsgListener()
    protected val systemMsgMonitor = ImActionMsgListener()
    protected val c2cMsgMonitor = ImActionMsgListener()

    init {
        LivingRoomManager.addMonitor(this)
        channeMsgMonitor.attach()
        ImMsgDispatcher.addC2CListener(c2cMsgMonitor)
        ImMsgDispatcher.systemMsgListener.add(systemMsgMonitor)

        monitorImAction()
    }

    open override fun onRoomSessionChange(roomSession: RoomSession) {
        //切换监听的群
      //  channeMsgMonitor.groupId = roomSession.getImGroupId()
    }

    override fun onResumeFromMin(roomSession: RoomSession) {
        if (channeMsgMonitor.isAttach) {
            channeMsgMonitor.attach()
            monitorImAction()
        }
        //切换监听的群
        channeMsgMonitor.groupId = roomSession.getImGroupId()
    }

    abstract fun monitorImAction()

    override fun onCleared() {
        super.onCleared()
        channeMsgMonitor.dettach()
        LivingRoomManager.removeMonitor(this)
        ImMsgDispatcher.removeC2CListener(c2cMsgMonitor)
        ImMsgDispatcher.systemMsgListener.remove(systemMsgMonitor)
    }

    /**
     * 关闭房间
     */
    override fun onCloseRoom() {}



}