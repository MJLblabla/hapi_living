package com.hapi.im

import com.hapi.im.been.GroupMsg
import com.hapi.im.been.IMsgBean
import com.hapi.im.parser.*
import com.tencent.imsdk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * 消息在协成里解析完成 分发
 */
object ImMsgDispatcher {

    val sysImParsers = ArrayList<SystemImParser>()
    val goupImParsers = ArrayList<GroupImParser>()
    val c2cMsgParsers = ArrayList<C2cMsgParser>()



    /**
     * 系统消息拦截器
     * 用于外外层拦截 如果消息被处理页面就不会收到
     */
    var sysImMsgInterceptor: ImMsgInterceptor? = null
    var groupImMsgInterceptor: ImMsgInterceptor? = null
    var c2CimMsgInterceptor: ImMsgInterceptor? = null

    val groupImMsgLister = ArrayList<GroupImMsgLister>();
    private val c2CimMsgListener = ArrayList<ImMsgListener>()
    val systemMsgListener = ArrayList<ImMsgListener>()

    fun addC2CListener(imMsgListener: ImMsgListener){
        c2CimMsgListener.add(imMsgListener)
    }

    fun removeC2CListener(imMsgListener: ImMsgListener){
        c2CimMsgListener.remove(imMsgListener)
    }

    fun onNewMsg(msg: TIMMessage) {
        GlobalScope.launch(Dispatchers.Main) {


            when (msg.conversation?.type) {

                TIMConversationType.System -> {
                    var asyncBeen: IMsgBean? = null
                    val job = async {
                        sysImParsers.forEach {
                            asyncBeen = it.parse(msg)
                            if (asyncBeen !== null) {
                                return@forEach
                            }
                        }
                        asyncBeen
                    }

                    val been = job.await() ?: return@launch
                    if (sysImMsgInterceptor?.onNewMsg(been!!) == true) {
                        return@launch
                    }
                    systemMsgListener?.forEach {
                        it.onNewMsg(been!!)
                    }

                }

                TIMConversationType.Group -> {

                    var asyncBeen: GroupMsg? = null
                    val job = async {
                        goupImParsers.forEach {
                            asyncBeen = it.parseTIMElem(msg)
                            if (asyncBeen !== null) {
                                return@forEach
                            }
                        }
                        asyncBeen
                    }

                    val been = job.await() ?: return@launch
                    if (groupImMsgInterceptor?.onNewMsg(been!!) == true) {
                        return@launch
                    }
                    groupImMsgLister?.forEach {
                        it.onNewMsg(been!!)
                    }


                }

                TIMConversationType.C2C -> {
//                    for (i in 0..msg.elementCount) {
//                        val elem = msg.getElement(i)

                    val job = async {
                        var asyncBeen: IMsgBean? = null
                        c2cMsgParsers.forEach {
                            asyncBeen = it.parse(msg)
                            if (asyncBeen !== null) {
                                return@forEach
                            }
                        }
                        asyncBeen
                    }
                    val been = job.await() ?: return@launch
                    if (c2CimMsgInterceptor?.onNewMsg(been) == true) {
                        return@launch
                    }
                    c2CimMsgListener.forEach {
                        it.onNewMsg(been)
                    }
                }

                else -> {

                }
            }
        }
    }

}





