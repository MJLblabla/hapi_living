package com.hapi.absroom.live

import android.util.Log
import com.hapi.asbroom.RoleType
import com.hapi.asbroom.RoomSession
import com.hipi.absrrom.AbsRoomManager
import com.pince.im.ImCallback
import com.pince.im.ImManager
import com.tencent.imsdk.TIMCallBack
import com.tencent.imsdk.TIMGroupManager
import com.tencent.imsdk.TIMValueCallBack

object LivingRoomManager : AbsRoomManager() {




    fun startLiverLiving(roomSession: RoomSession){
      disPatchChangeRoom(roomSession)
    }


    fun dealImGroup(roomSession: RoomSession){
        val param =
            TIMGroupManager.CreateGroupParam("AVChatRoom", roomSession.getImGroupId())
        param.groupId = roomSession.getImGroupId()
        val groupId = roomSession.getImGroupId()

        val imWork = {
            if(roomSession.isNeedCreateImGroup()){
                TIMGroupManager.getInstance().createGroup(param, object : TIMValueCallBack<String> {
                    override fun onSuccess(p0:String) {
                        Log.d("hhq","加群成功--${groupId}")
                        if (groupId != this@LivingRoomManager.roomSession?.getImGroupId()) {
                            quitImGroup(roomSession)
                        } else {

                            monitors.forEach {
                                it.onRoomJoined(roomSession)
                            }
                        }
                    }


                    override fun onError(code: Int, desc: String?) {
                        Log.d("hhq","加群失败--${groupId}")
                        monitors.forEach {
                            it.onRoomJoinFail(code,roomSession)
                        }
                    }
                })
            }else{

                TIMGroupManager.getInstance().applyJoinGroup(groupId, "",
                    object : TIMCallBack {
                        override fun onSuccess() {
                            Log.d("hhq","加群成功--${groupId}")
                            if (groupId != this@LivingRoomManager.roomSession?.getImGroupId()) {
                                quitImGroup(roomSession)
                            } else {

                                monitors.forEach {
                                    it.onRoomJoined(roomSession)
                                }
                            }
                        }


                        override fun onError(code: Int, desc: String?) {
                            Log.d("hhq","加群失败--${groupId}")
                            monitors.forEach {
                                it.onRoomJoinFail(code,roomSession)
                            }
                        }
                    })
            }
        }

            ImManager.checkLogin(object :ImCallback{
                override fun onFail(code: Int, msg: String?) {
                    monitors.forEach {
                        it.onRoomJoinFail(code,roomSession)
                    }
                }

                override fun onSuc() {}

            }) {
                imWork.invoke()
            }


    }
    /**
     * 房主创建房间 (自己加入自己的房间)
     * //无须自己创建群 所有创建群由后台操作
     */
    fun createRoom(roomSession: RoomSession) {

        if (this.roomSession != null) {
          //  throw Exception("上一个房间还没退出")
            Log.d("createRoom","上一个房间还没退出")
            this.roomSession != null
        }

        dispatchCreateRoomChecked(roomSession)

        dealImGroup(roomSession)
    }

    /**
     * 用户加入房间
     */
    fun enterRoom(roomSession: RoomSession) {
        if (roleType == RoleType.type_room_owner) {
            throw java.lang.Exception("房主不能切换房间")
        }
        this.roomSession?.let {
            quitImGroup(it)
        }

        dispatchEnterRoomChecked(roomSession)

        val groupId = roomSession.getImGroupId()
        TIMGroupManager.getInstance().applyJoinGroup(groupId, "",
            object : TIMCallBack {
                override fun onSuccess() {
                    Log.d("hhq","加群成功--${groupId}")
                    if (groupId != this@LivingRoomManager.roomSession?.getImGroupId()) {
                        quitImGroup(roomSession)
                    } else {

                        monitors.forEach {
                            it.onRoomJoined(roomSession)
                        }
                    }
                }


                override fun onError(code: Int, desc: String?) {
                    Log.d("hhq","加群失败--${groupId} -${code} -${desc} ")
                    monitors.forEach {
                        it.onRoomJoinFail(code,roomSession)
                    }
                }
            })

    }


    fun closeRoom() {
        if (roomSession != null) {
            if (roleType == RoleType.type_room_owner) {
                deleteGroup()
            } else {
                quitImGroup(roomSession!!)
            }
        }

        disPatchCloseRoom()
    }

    fun minWindow() {
        disPatchMinWindow()
    }


    override fun disPatchChangeRoom(roomSession: RoomSession) {
        this.roomSession?.let {
            quitImGroup(it)
        }
        super.disPatchChangeRoom(roomSession)
    }

    private fun deleteGroup() {
        TIMGroupManager.getInstance()
            .deleteGroup(roomSession!!.getImGroupId(), object : TIMCallBack {
                override fun onSuccess() {
                }

                override fun onError(code: Int, desc: String?) {
                }
            })
    }


    private fun quitImGroup(roomSession: RoomSession) {
        TIMGroupManager.getInstance().quitGroup(roomSession.getImGroupId(), object : TIMCallBack {
            override fun onSuccess() {
            }

            override fun onError(code: Int, desc: String?) {
            }
        })
    }


}