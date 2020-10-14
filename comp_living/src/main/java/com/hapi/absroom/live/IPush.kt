package com.hapi.absroom.live

interface IPush {

    /**
     * 开启预览
     */
    fun startPreView()

    /**
     * 开始推流
     */
    fun startPush(url: String)
    fun stopPush()

}