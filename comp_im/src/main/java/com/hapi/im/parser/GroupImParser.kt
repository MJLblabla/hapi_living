package com.hapi.im.parser

import com.hapi.im.been.GroupMsg
import com.tencent.imsdk.TIMMessage

interface GroupImParser {
    fun parseTIMElem(msg: TIMMessage): GroupMsg?
}