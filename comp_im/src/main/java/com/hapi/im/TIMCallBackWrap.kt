package com.hapi.im

import android.text.TextUtils
import android.util.Log
import com.tencent.imsdk.TIMCallBack
import com.tencent.imsdk.TIMMessage
import com.tencent.imsdk.TIMValueCallBack

class TIMCallBackWrap(private val callback: ImCallback?) : TIMCallBack {
    /**
     * 成功时回调
     */
    override fun onSuccess() {
        callback?.onSuc()
    }

    /**
     * 出错时回调
     *
     * @param code 错误码，详细描述请参见错误码表
     * @param desc 错误描述
     */
    override fun onError(code: Int, desc: String?) {
        callback?.onFail(code,desc)
    }
}

class TIMValueCallBackWarp(private val callback: ImCallback?) : TIMValueCallBack<TIMMessage> {


    /**
     * 出错时回调
     *
     * @param code 错误码，详细描述请参见错误码表
     * @param desc 错误描述
     */
    override fun onError(code: Int, desc: String?) {
        callback?.onFail(code, desc)
        Log.d("TIMValueCallBackWarp","TIMValueCallBackWarp ${code} ${desc}")
        if(code==6071 ){
            ImManager.reLogin(null)
        }
    }

    /**
     * 成功时回调
     */
    override fun onSuccess(t: TIMMessage?) {
        callback?.onSuc()
    }
}