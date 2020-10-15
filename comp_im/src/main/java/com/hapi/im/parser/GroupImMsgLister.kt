package com.hapi.im.parser

import com.hapi.im.been.GroupMsg

interface GroupImMsgLister {
    fun onNewMsg(msg: GroupMsg)
}