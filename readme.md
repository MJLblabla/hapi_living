## 直播房间架构 ##


　　![此处输入图片的描述][1]

　房间状态监听

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
        fun onRoomJoinFail(roomSession: RoomSession){}
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
    
    
    抽象房间roomsession
 

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
    }
    

  图一中红的组件都实现了状态监听。

  总体设计思想：抽象层负责房间切换群操作，与房间生命周期分发。
  业务层根据房间状态各司其职，避免冗余爆炸交互，因为房间业务本来庞大复杂
  
  具体实现请参考lk直播间的优秀设计部分
  
  
  
  
  

## 源码解析 ##  

观众端：AbsLivingRoomActivity

抽象层：　－>

AbsLivingRoomActivity.kt
布局：RecyclerView　加一个用于扩展的flCoverContent

    <FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>


    <FrameLayout
        android:id="@+id/flCoverContent"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="gone" />
</FrameLayout>

初始化：

      private fun initRecyView() {
        recyclerView.layoutManager = mLayoutManager
        recyclerView.animation = null
        recyclerView.adapter = adapter
        adapter.setNewData(roomSessions)
        recyclerView.scrollToPosition(currentPosition)
        Log.d("hhq", " scrollToPosition   position---$currentPosition")
        mLayoutManager.scrollToPositionWithOffset(currentPosition, 0)
    }
    
　初始化RoomAdapter和mLayoutManager
  
  
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
  
  
    setViewGroup(mRoomContentView, ....  mRoomContentView　 就是房间正真的内容在抽象层不知道内容是啥所以是抽象的，RoomAdapter只负责了封面的加载
    
  可以看到这里PagerLayoutManager　在抽象层处理好了页面滑动当页面滑动然后调用了 LivingRoomManager.disPatchChangeRoom(currentRoomSession)
  在进入方法之前我先看看LivingRoomManager是啥？
  　他维护了当前房间信息和所以监听者列表

    protected val monitors = CopyOnWriteArrayList<RoomStatusMonitor>()//房间状态监听列表
    /**
     * 当前房间信息
     */
    var roomSession: RoomSession? = null
        private set

    /**
     * 是否在房间
     */
    var roomStatus = RoomStatus.status_default
        private set

    var roleType = RoleType.Type_room_visitor　//房间类型


    fun addMonitor(monitor: RoomStatusMonitor) {　
        monitors.add(monitor)
    }

    fun removeMonitor(monitor: RoomStatusMonitor) {
        monitors.remove(monitor)
    }
  
  
  
  进入这个方法看看
　　LivingRoomManager.kt
　　
　　

    override fun disPatchChangeRoom(roomSession: RoomSession) {
        this.roomSession?.let {
            quitImGroup(it)
        }
        super.disPatchChangeRoom(roomSession)
    }
    处理逻辑为如果当前房间还在先退出房间，然后调用了父类的逻辑
    AbsRoomManager.kt
    
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
    分发房间离开事件，然后分发房间进入事件
　　在这个过程，业务层收到事件可以处理的事情比如清空headview,停止拉流，清空加载列表等等
　　进入房间当前会和服务交互验证房间等等，所以这一步在absRoomVm做了抽象处理：
　　
　　AbsRoomVm.kt
    
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

    当业务层回调能进入房间后，这里根据房间类型做加入房间和创建房间相应的操作：
    
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
        val param =
            TIMGroupManager.CreateGroupParam("AVChatRoom", roomSession.getImGroupName())
        param.groupId = roomSession.getImGroupId()
        dispatchCreateRoomChecked(roomSession)

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
                    Log.d("hhq","加群失败--${groupId}")
                    monitors.forEach {
                        it.onRoomJoinFail(roomSession)
                    }
                }
            })
        


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
                        it.onRoomJoinFail(roomSession)
                    }
                }
            })

    }
    
    
可以看到这异步先调用    dispatchEnterRoomChecked（）给下层分发房间验证成功了，下层可以去跟新房间成员开始推拉流等等操作。然后LivingRoomManager根据当前抽象的roomsetion去加群


以上是房间一部分的逻辑，像小窗全屏等等可以去查阅源码分析，这里演示的只是为读者说明，整体设计就是把状态分发给各个页面　各个页面处理自己的关心的业务避免各个页面传递事件和交互


## 轨道管理组件 ##
　可包括弹幕轨道礼物轨道飘瓶轨道，大礼队列
　
　
　　抽象礼物view
    interface TrackView<T> {
    
        var finishedCall :(()->Unit) ?
        /**
         * 是不是同一个轨道上的
         */
        fun showInSameTrack(giftTrackMode: T):Boolean
    
    
        /**
         * 显示礼物
         */
        fun onNewModel(mode: T)
        /**
         * 是不是忙碌
         */
        fun isShow():Boolean
        /**
         * 退出直播间或者切换房间 清空
         */
        fun clear(isRoomChange:Boolean=false)
    }

    　　
    /**
     * 轨道控制
     */
    class TrackManager<T> : RoomStatusMonitor {
    
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
                           delay(500)
                        }else{
                            trackModeQueue.pop()
                            if(trackModeQueue.isEmpty()){
                                next = false
                                job = null
                            }else{
                                delay(500)
                            }
                        }
    
                    }else{
                        next = false
                        job = null
                    }
                }
    
            }
        }
    
        fun onNewTrackArrive(giftTrackMode: T) {
    
            Log.d("TrackManager","onNewTrackArrive")
            trackModeQueue.add(giftTrackMode)
    
            if(job==null){
                newJob()
                job?.start()
            }
    
        }
    
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


把ui布局的轨道view托管起来，每次来任务遍历一遍有没有能处理的
没有空闲的轨道，当房间切换是清空轨道，当房间关闭时释放轨道


业务层使用案例lucklive：

    class ChannelMsgVm(application: Application, bundle: Bundle?) : AbsChannelVm(application, bundle) {
         
        val danmakuTrackManager: DanmukManager<Danmuke> by lazy {　　//弹幕轨道
            DanmukManager<Danmuke>().apply { init() }
        }
    
        val broadcastTrackManager: TrackManager<RoomBroadcast> by lazy {　//广播轨道
            TrackManager<RoomBroadcast>().apply { init() }
        }
      val imGiftTrackManager: TrackManager<ImGroupGiftModel> by lazy {　//幸运礼物轨道
            TrackManager<ImGroupGiftModel>().apply { init() }
        }
        
         override fun monitorImAction() {
         
         　　  //监听礼物消息
            channeMsgMonitor.onOptAction<CustomGroupMsgBeen<ImGroupGiftModel>>(
                subType_luck_gift_small_group
            ) {
               imGiftTrackManager.onNewTrackArrive(it.data)
            }
            
              //幸运飘萍
            channeMsgMonitor.onOptAction<CustomGroupMsgBeen<ImGroupGiftModel>>(
                subType_broadcast_luck_win_big_group
            ){
               broadcastTrackManager.onNewTrackArrive(RoomLuckBroadcast(it.data))
            }
           
             //弹幕
            channeMsgMonitor.onOptAction<CustomGroupMsgBeen<Danmuke>>(
                subType_danmuke_small_group
            ) {
                danmakuTrackManager.onDanmukArrived(it.data)
            }
         ｝
    }
　　
　　ChannelMsgVm部分代码收到消息尽管往里面添加事件就行了，那轨道view怎么添加进去：
　　RoomCoverView覆盖页initViewData方法把实现动画的view添加进去就行了，动画交给你来实现，队列管理就给TrackManager搞定
　　

    abstract class RoomCoverView : BaseVmFragment(), RoomStatusMonitor {
    　　    
    　　override fun initViewData() {
    　　    imGiftTrackManager.addTrackView(giftShowViewStub1)
            imGiftTrackManager.addTrackView(giftShowViewStub2)
            danmakuTrackManager.addView(DanmukViewStub(stubDanmuk1))
            danmakuTrackManager.addView(DanmukViewStub(stubDanmuk2))
            broadcastTrackManager.addTrackView(BroadcastViewStub(stubBroacasta, BroadcastTrackTag.A))
            broadcastTrackManager.addTrackView(BroadcastViewStub(stubBroacastb, BroadcastTrackTag.B))　　    
    　　 }
     }
  
    
 状态分发几乎可以让各个组件在自己关系的状态做自己的事
    
![此处输入图片的描述][2]
    
    
## Im组件 ##
　
　　架构思想：在抽象层里把javabeen 在子线程里解析好，然后分发给主线程的各个监听者，只解析一次，各个页面直接使用
　　
　　![此处输入图片的描述][3]
　　
　　接入指南：
　　



业务层层接入：
-------
　初始化配置


     fun init(context: Application, timAppId: Int) {
        val userConfig = TIMUserConfig()
            .setUserStatusListener(object : TIMUserStatusListener {
                override fun onUserSigExpired() {
                    Toast.makeText(
                        ActivityManager.get().currentActivity(),
                        context.getString(R.string.usersign_exceed_limit),
                        Toast.LENGTH_SHORT
                    ).show()
                    UserInfoManager.onLogout()
                    PRouter.openUrl(
                        ActivityManager.get().currentActivity(),
                        RouterConstant.Login.LOGIN
                    )
                }

                override fun onForceOffline() {
                    Log.d("hhq", "在其他设备登录")
                    Toast.makeText(
                        ActivityManager.get().currentActivity(),
                        context.getString(R.string.login_anthor_device),
                        Toast.LENGTH_SHORT
                    ).show()
                    UserInfoManager.onLogout()
                    PRouter.openUrl(
                        ActivityManager.get().currentActivity(),
                        RouterConstant.Login.LOGIN
                    )
                }

            })
            .setConnectionListener(object : TIMConnListener {
                override fun onConnected() {
                }

                override fun onWifiNeedAuth(p0: String?) {}
                override fun onDisconnected(p0: Int, p1: String?) {

                }
            })
            .setRefreshListener(object : TIMRefreshListener {
                override fun onRefreshConversation(p0: List<TIMConversation>) {

                }

                override fun onRefresh() {
                }

            })
            .disableAutoReport(true)
            .enableReadReceipt(true)
            .setMessageReceiptListener {

            }

        val config = TIMSdkConfig(timAppId)
            .enableLogPrint(true)

        //监听用户状态登录登出
        ImManager.init(context, userConfig, config)
        UserStateDistribute.addListener(object : UserStateDistribute.UserStateListener {
            override fun onUserLogin(user: LoginModel) {
            }

            override fun onUserLoginOut(user: LoginModel) {
                ImManager.loginOut(null)
            }

            override fun onUserInfoChange() {
            }
        })

        //群消息拦截
        ImMsgDispatcher.groupImMsgInterceptor = object : ImMsgInterceptor {
            override fun onNewMsg(msg: IMsgBean): Boolean {
                return false
            }
        }

        // 系统消息拦截
        ImMsgDispatcher.sysImMsgInterceptor = object : ImMsgInterceptor {

            override fun onNewMsg(msg: IMsgBean): Boolean {
                return false
            }
        }
        // 私聊消息拦截
        ImMsgDispatcher.c2CimMsgInterceptor = object : ImMsgInterceptor {
            override fun onNewMsg(msg: IMsgBean): Boolean {
                return false
            }
        }
        ImMsgDispatcher.goupImParsers.add(GroupMessageParser())

        ImMsgDispatcher.sysImParsers.add(SysMessageParser())

        ImMsgDispatcher.c2cMsgParsers.add(C2cMessageParser())
    }

　　解析器案例：　以luckLive为例 (lk的群消息｛type:"",data:{} ｝　)
　　

    
class GroupMessageParser : GroupImParser {

    override suspend fun parseTIMElem(msg: TIMMessage): GroupMsg? {
        Log.d("hhq", "群消息")
        var e: TIMCustomElem? = null
        var been: CustomGroupMsgBeen<*>? = null
        val ele = msg.getElement(0)
        if (ele.type == TIMElemType.Custom) {
            e = ele as TIMCustomElem?
        }
        if (e == null) {
            return null
        }
        e?.let {
            val str = String(it.data)
            Log.d("hhq", "群消息内容 ${str}")
            try {
                val jb: JSONObject = JSONObject(str)
                val type: Int = jb.opt("type").toString().toInt()
                var jsonType: ParameterizedTypeImpl? = null


                when(type){

                    subType_be_add_black ->{
                        jsonType = ParameterizedTypeImpl(
                            arrayOf<Type>(ActionBlack::class.java),
                            CustomGroupMsgBeen::class.java,
                            CustomGroupMsgBeen::class.java
                        )
                        been = JsonUtil.fromJson<CustomGroupMsgBeen<ActionBlack>>(str, jsonType)
                    }

                    subType_be_forbidden_say ->{
                        jsonType = ParameterizedTypeImpl(
                            arrayOf<Type>(ActionForbidden::class.java),
                            CustomGroupMsgBeen::class.java,
                            CustomGroupMsgBeen::class.java
                        )
                        been = JsonUtil.fromJson<CustomGroupMsgBeen<ActionForbidden>>(str, jsonType)
                    }

                    in 101..199 -> {
                        jsonType = ParameterizedTypeImpl(
                            arrayOf<Type>(ImGroupGiftModel::class.java),
                            CustomGroupMsgBeen::class.java,
                            CustomGroupMsgBeen::class.java
                        )
                        been = JsonUtil.fromJson<CustomGroupMsgBeen<ImGroupGiftModel>>(str, jsonType)
                    }


　　


　　　　群消息监听器案例：
　　　　 

       protected val channeMsgMonitor = ImGroupActionMsgListener()
    　　　　　  channeMsgMonitor.attach()//注册
    　　　　    channeMsgMonitor.dettach()//页面销毁取消注册

　　　　

     //被拉黑
        channeMsgMonitor.onOptAction<CustomGroupMsgBeen<ActionBlack>>(subType_be_add_black) {
            Log.d("hhq", "subType_be_add_black")
            //toast()
            if (it.data.receiverId == UserInfoManager.getUserId()) {
                toast(it.data.message?.suitLocal())
                viewModelScope.launch(Dispatchers.Main) {
                    LivingRoomManager.closeRoom()
                }
            }
        }

        channeMsgMonitor.onOptAction<CustomGroupMsgBeen<ActionForbidden>>(subType_be_forbidden_say) {
            if (it.data.roomId == LivingRoomManager.roomSession?.getRoomId() ?: ""
                && it.data.receiverId==UserInfoManager.getUserId()
            ) {
                chatLineCall?.invoke(it.data)
            }
        }

        channeMsgMonitor.onOptAction<CustomGroupMsgBeen<EmptyRoomMsg>>(subType_room_member_updata) {
            updateUserWatcherLiveData.value = Unit
        }
        //文本公聊
        channeMsgMonitor.onOptAction<CustomGroupMsgBeen<RoomChatTextMsg>>(
            subType_chat_room_small_group
        ) {
            if (it.data.roomId == LivingRoomManager.roomSession?.getRoomId() ?: "") {
                chatLineCall?.invoke(it.data)
            }
        }

　　


  [1]: https://raw.githubusercontent.com/MJLblabla/hapi_living/master/room_art.png
  [2]: https://raw.githubusercontent.com/MJLblabla/hapi_living/master/img/func.png
  [3]: https://raw.githubusercontent.com/MJLblabla/hapi_living/master/im.png