package com.hapi.im.parser

import com.hapi.im.been.IMsgBean

interface ImMsgInterceptor  {
    fun onNewMsg(msg: IMsgBean):Boolean
}