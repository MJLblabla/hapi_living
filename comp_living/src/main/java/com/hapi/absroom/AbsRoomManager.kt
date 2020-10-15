package com.hipi.absrrom

import com.hapi.absroom.func.RoomTimer
import com.hapi.asbroom.RoleType
import com.hapi.asbroom.RoomSession
import com.hapi.asbroom.RoomStatus
import com.hapi.asbroom.RoomStatusMonitor
import com.hapi.asbroom.roomui.bigGift.BigGiftManager
import com.hapi.asbroom.roomui.trackGift.TrackManager
import java.util.concurrent.CopyOnWriteArrayList

abstract class AbsRoomManager {

    protected val monitors = CopyOnWriteArrayList<RoomStatusMonitor>()
    /**
     * 当前房间信息
     */
    var roomSession: RoomSession? = null
        private set

     fun initSetLiverRoomSession( roomSession: RoomSession){
        this.roomSession = roomSession
    }
    /**
     * 是否在房间
     */
    var roomStatus = RoomStatus.status_default
        private set

    var roleType = RoleType.Type_room_visitor


    fun addMonitor(monitor: RoomStatusMonitor) {
        monitors.add(monitor)
    }

    fun removeMonitor(monitor: RoomStatusMonitor) {
        monitors.remove(monitor)
    }

    fun clear(){
        monitors.clear()
    }
    protected fun dispatchEnterRoomChecked(roomSession: RoomSession) {
        this.roomSession = roomSession
        this.roleType = RoleType.Type_room_visitor
        roomStatus = RoomStatus.status_in_Room
        disPatchRoomChecked(roomSession)
    }

    protected fun dispatchCreateRoomChecked(roomSession: RoomSession) {
        this.roomSession = roomSession
        this.roleType = RoleType.type_room_owner
        roomStatus = RoomStatus.status_in_Room
        disPatchRoomChecked(roomSession)
    }

    protected fun disPatchCloseRoom() {
        roomStatus = RoomStatus.status_default
        monitors.forEach {
            it.onCloseRoom()
        }
        this.roomSession = null
    }

    open fun disPatchChangeRoom(roomSession: RoomSession) {
        this.roomSession?.let { session ->
            monitors.forEach {
                it.onRoomLevel(session)
            }
        }
        this.roomSession = null
        monitors.forEach {
            it.onRoomSessionChange(roomSession)
        }
    }



    open fun disPatchRoomChecked(roomSession: RoomSession){
        monitors.forEach {
            it.onRoomChecked(roomSession)
        }
    }

    fun resumeFromMinWindow(roomSession: RoomSession) {
        roomStatus = RoomStatus.status_in_Room
        this.roomSession = roomSession
        monitors.forEach {
            it.onResumeFromMin(roomSession)
        }
    }


    protected fun disPatchMinWindow() {
        roomStatus = RoomStatus.status_min_window
        monitors.forEach {
            it.onMinimizeWindow()
        }
    }


}