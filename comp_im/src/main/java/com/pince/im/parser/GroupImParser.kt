package com.pince.im.parser

import com.pince.im.been.GroupMsg
import com.tencent.imsdk.TIMMessage

interface GroupImParser {
   suspend fun parseTIMElem(msg: TIMMessage): GroupMsg?
}