package com.hapi.asbroom.roomui.bigGift

import android.view.View

interface BigGiftView<T> {

    var finishedCall :(()->Unit) ?
    fun getView(): View
    fun isPlaying():Boolean
    fun clear()

    /**
     * 能
     */
    fun playIfPlayAble(gigGiftMode:T):Boolean
}