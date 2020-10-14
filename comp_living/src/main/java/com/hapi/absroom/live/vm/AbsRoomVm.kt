package com.hapi.asbroom.audiolive

import android.app.Application
import android.os.Bundle
import com.hapi.absroom.func.RoomTimer
import com.hapi.absroom.live.LivingRoomManager
import com.hapi.asbroom.RoleType
import com.hapi.asbroom.RoomSession
import com.hapi.asbroom.RoomStatusMonitor
import com.hipi.vm.BaseViewModel

/**
 *
 */
abstract class AbsRoomVm(application: Application, bundle: Bundle?) :
    BaseViewModel(application), RoomStatusMonitor {

    init {
        LivingRoomManager.addMonitor(this)
        RoomTimer.init()
    }

    override fun onCleared() {
        super.onCleared()

        LivingRoomManager.removeMonitor(this)
    }

    /**
     * 房间切换后业务请求
     * @param block 返回能不能进去频道
     */
    abstract fun afterRoomSessionChange(
        roleType: RoleType,
        isFromMin: Boolean,
        roomSession: RoomSession,
        block: (needIntoRoom: Boolean) -> Unit
    )



    final override fun onRoomSessionChange(roomSession: RoomSession) {
        afterRoomSessionChange(LivingRoomManager.roleType, false, roomSession) {
            if(it){
                if (LivingRoomManager.roleType == RoleType.type_room_owner) {
                    LivingRoomManager.createRoom(roomSession)
                } else {
                    LivingRoomManager.enterRoom(roomSession)
                }

            }
        }
    }


    final override fun onResumeFromMin(roomSession: RoomSession) {
        afterRoomSessionChange(LivingRoomManager.roleType, true, roomSession){}
    }

    /**
     * 关闭房间
     */
    override fun onCloseRoom() {

    }

    /**
     * 最小化
     */
    override fun onMinimizeWindow() {

    }
}