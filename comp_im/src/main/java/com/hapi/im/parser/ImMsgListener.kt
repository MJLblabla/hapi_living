package com.hapi.im.parser

import com.hapi.im.been.IMsgBean

interface ImMsgListener {

    fun onNewMsg(msg: IMsgBean)

}