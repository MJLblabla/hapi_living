package com.hapi.absroom.live

import com.hapi.asbroom.RoleType
import com.hapi.asbroom.RoomSession
import com.hapi.asbroom.RoomStatus
import com.hapi.asbroom.RoomStatusMonitor
import com.hapi.base_mvvm.mvvm.BaseVmActivity

abstract class AbsLiverRoomActivity <T : RoomSession> : BaseVmActivity(), RoomStatusMonitor {


    override fun isToolBarEnable(): Boolean {
        return false
    }

    var roleType: RoleType = RoleType.type_room_owner

    abstract fun initBundle()

    override fun initViewData() {
        initBundle()
        LivingRoomManager.roleType = roleType
        LivingRoomManager.addMonitor(this)


    }



    override fun onDestroy() {
        super.onDestroy()
        if(LivingRoomManager.roomSession!=null){
            LivingRoomManager.closeRoom()
        }
        LivingRoomManager.removeMonitor(this)
        LivingRoomManager.clear()
    }

}