package com.hapi.asbroom

import com.hapi.absroom.live.LivingRoomManager

interface RoomSession{
    //声网房间配置
    fun getRoomId():String
    fun getHostUid():String
    fun getMeUid():Int
    //im配置
    fun getImGroupName():String
    fun getImGroupId():String
    fun getPushUri():String
    fun getPullUri():String
    /**
     * 房间封面
     */
    fun getRoomCoverImg():String

    fun isNeedCreateImGroup():Boolean{
        return LivingRoomManager.roleType==RoleType.type_room_owner

    }
}