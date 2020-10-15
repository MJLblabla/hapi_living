package com.hapi.im.parser

import com.hapi.im.been.IMsgBean
import com.tencent.imsdk.TIMMessage

interface SystemImParser {
    fun parse(msg: TIMMessage): IMsgBean?
}