package com.pince.im

interface ImCallback {
    fun onSuc()
    fun onFail(code:Int,msg:String?)
}