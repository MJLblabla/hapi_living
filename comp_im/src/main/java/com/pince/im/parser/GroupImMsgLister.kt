package com.pince.im.parser

import com.pince.im.been.GroupMsg
import com.pince.im.been.IMsgBean

interface GroupImMsgLister {
    fun onNewMsg(msg: GroupMsg)
}