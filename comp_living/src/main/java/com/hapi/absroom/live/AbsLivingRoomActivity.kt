package com.hapi.absroom.live

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.OrientationHelper
import com.hapi.absroom.R
import com.hapi.absroom.weight.PagerLayoutManager
import com.hapi.asbroom.RoleType
import com.hapi.asbroom.RoomSession
import com.hapi.asbroom.RoomStatus
import com.hapi.asbroom.RoomStatusMonitor
import com.hapi.asbroom.audiolive.AbsChannelVm
import com.hapi.asbroom.audiolive.RoomAdapter
import com.hapi.asbroom.weight.VerticalAdapter
import com.hapi.base_mvvm.mvvm.BaseVmActivity
import com.hipi.vm.lazyVm
import kotlinx.android.synthetic.main.activity_abs_audio_room.*

abstract class AbsLivingRoomActivity<T : RoomSession> : BaseVmActivity(), RoomStatusMonitor {


    /**
     * 默认房间适配器 
     */
    open var adapter: RoomAdapter<T> = RoomAdapter()
    abstract var currentPosition: Int
    abstract var roomSessions: List<T>
    var roleType: RoleType = RoleType.Type_room_visitor

    /**
     * 根据房间数据源 初始化当前mRoomContentView
     */
    abstract fun initRoomSessionView(currentRoomSession: RoomSession)

    /**
     * 真正房间布局
     */
    abstract val mRoomContentView: ViewGroup


    /**
     * 覆盖层
     */
    open fun getCoverLayout(parent: ViewGroup?): View? {
        return null
    }


    /**
     * 下一个主播
     */
    fun jumpTop() {
       // if (adapter.data.size >= 1) {


        //recyclerView.scrollToPosition(currentPosition + 1)
          //  mLayoutManager.scrollToPositionWithOffset(currentPosition+1, 0)
        //}

        recyclerView.scrollToPosition(0)
        mLayoutManager.scrollToPositionWithOffset(0, 0)

        currentPosition = 0
       // Log.d("hhq", " onSelect   position---$position")
        val currentRoomSession = roomSessions[0]
        LivingRoomManager.disPatchChangeRoom(currentRoomSession)

    }

    private val mLayoutManager by lazy {
        PagerLayoutManager(this, OrientationHelper.VERTICAL)
            .apply {
                setViewGroup(mRoomContentView, object : PagerLayoutManager.IreloadInterface {
                    override fun onDestroyPage(isNext: Boolean, position: Int, view: View?) {
                    }

                    override fun onReloadPage(position: Int, isBottom: Boolean, view: View?) {

                        var p = position
                        if (p == -1000) {
                            p = currentPosition
                            onSelect(p, view, true)
                            return
                        }

                        if (p != currentPosition) {
                            onSelect(p, view, false)
                        }
                    }

                })
            }
    }


    /**
     * @param isFirstTime 首次进入
     */
    open fun onSelect(position: Int, v: View?, isFist: Boolean) {
        currentPosition = position
        Log.d("hhq", " onSelect   position---$position")
        val currentRoomSession = roomSessions[position]
        val isFromMin = isFist
                && LivingRoomManager.roomStatus == RoomStatus.status_min_window
                && LivingRoomManager.roomSession?.getRoomId() == currentRoomSession.getRoomId()
        if (isFromMin) {
            LivingRoomManager.resumeFromMinWindow(currentRoomSession)
        } else {
            LivingRoomManager.disPatchChangeRoom(currentRoomSession)
        }
    }


    override fun isToolBarEnable(): Boolean {
        return false
    }

    override fun initViewData() {
        LivingRoomManager.roleType = roleType
        LivingRoomManager.addMonitor(this)
        getCoverLayout(flCoverContent)?.let {
            flCoverContent.addView(it)
            flCoverContent.visibility = View.VISIBLE
        }

        initRecyView()
        initOtherView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(LivingRoomManager.roomSession!=null){
            LivingRoomManager.closeRoom()
        }
        LivingRoomManager.removeMonitor(this)
        LivingRoomManager.clear()
    }

    open fun initOtherView() {}

    private fun initRecyView() {
        recyclerView.layoutManager = mLayoutManager
        recyclerView.animation = null
        recyclerView.adapter = adapter
        adapter.setNewData(roomSessions)
        recyclerView.scrollToPosition(currentPosition)
        Log.d("hhq", " scrollToPosition   position---$currentPosition")
        mLayoutManager.scrollToPositionWithOffset(currentPosition, 0)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_abs_audio_room
    }

    override fun showLoading(toShow: Boolean) {

    }

    /**
     * 仅仅通知房间切换 (((((并没有加入房间成功的意思
     */
    override fun onRoomSessionChange(roomSession: RoomSession) {
        initRoomSessionView(roomSession)
    }


    /**
     * 最小化
     */
    override fun onMinimizeWindow() {
    }

    override fun onResumeFromMin(roomSession: RoomSession) {
        super.onResumeFromMin(roomSession)

    }

    override fun onRoomLevel(roomSession: RoomSession) {
        super.onRoomLevel(roomSession)
        Log.d("hhq", "离开房间")

    }

    override fun onRoomJoined(roomSession: RoomSession) {
        super.onRoomJoined(roomSession)
        Log.d("hhq", "加入房间")

    }


}
