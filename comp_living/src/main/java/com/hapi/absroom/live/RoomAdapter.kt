package com.hapi.asbroom.audiolive

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hapi.absroom.R
import com.hapi.asbroom.RoomSession
import com.hapi.asbroom.weight.VerticalAdapter
import kotlinx.android.synthetic.main.item_room.view.*
import java.util.logging.Logger

open class RoomAdapter<T:RoomSession> : VerticalAdapter<T>(R.layout.item_room) {


    /**
     * 如果player上面需要加布局　用这个
     */
    open fun getCoverLayout(parent: ViewGroup): View? {
        return null
    }
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder? {
        val vh = super.onCreateDefViewHolder(parent, viewType)
        val cl = getCoverLayout(parent)
        if (cl != null) {
            vh.itemView.flItemContent.addView(cl)
            vh.itemView.flItemContent.visibility = View.VISIBLE
        }
        return vh
    }

    override fun convert(helper: BaseViewHolder, item: T) {

        Glide.with(mContext).load(item.getRoomCoverImg())
            .into(helper.itemView.ivRoomCover)

    }

}