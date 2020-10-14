package com.hapi.asbroom

interface  RoomStatusMonitor {
    /**
     * 仅仅通知房间切换 (((((并没有加入房间成功的意思
     */
    fun onRoomSessionChange(roomSession: RoomSession)
    /**
     * 离开这个房间
     */
    fun onRoomLevel(roomSession: RoomSession){}
    /**
     * 服务器校验可以加房间后
     */
    fun onRoomChecked(roomSession: RoomSession){}
    /**
     * 加入成功
     */
    fun onRoomJoined(roomSession: RoomSession){}
    //加入房间失败
    fun onRoomJoinFail(code:Int,roomSession: RoomSession){}
    //从小窗恢复
    fun onResumeFromMin(roomSession: RoomSession){}
    /**
     * 关闭房间
     */
    fun onCloseRoom()
    /**
     * 最小化
     */
    fun onMinimizeWindow(){}

}