package com.pince.im.parser

import com.pince.im.been.IMsgBean
import com.tencent.imsdk.TIMMessage

interface C2cMsgParser {
    fun parse(msg: TIMMessage): IMsgBean?
}