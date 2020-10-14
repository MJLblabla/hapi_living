package com.hapi.absroom.live

interface IPull {
    /**
     * 开播
     */
    fun startPlay(uri: String)
    fun onStop()
}