package com.pince.im.parser

import com.pince.im.been.IMsgBean

interface ImMsgInterceptor  {
    fun onNewMsg(msg: IMsgBean):Boolean
}