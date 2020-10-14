package com.pince.im.parser

import com.pince.im.ImMsgDispatcher
import com.pince.im.been.IMsgBean

interface ImMsgListener {

    fun onNewMsg(msg: IMsgBean)

}